from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Optional

from pyspark.sql import SparkSession

from mediahub.config import JobConfig
from mediahub.logging_utils import setup_logging
from mediahub.ops.audit import AuditLogger


class SparkJob(ABC):
    def __init__(self, config: JobConfig):
        self.config = config
        self.logger = setup_logging(config.log_level)
        self.spark: Optional[SparkSession] = None

    def build_spark(self) -> SparkSession:
        spark = (
            SparkSession.builder
            .appName(self.config.app_name)
            .config("spark.sql.adaptive.enabled", str(self.config.adaptive_enabled).lower())
            .config("spark.sql.shuffle.partitions", str(self.config.shuffle_partitions))
            .config("spark.sql.broadcastTimeout", str(self.config.broadcast_timeout))
            .config("spark.driver.maxResultSize", self.config.driver_max_result_size)
            .getOrCreate()
        )

        spark.sparkContext.setLogLevel("WARN")
        return spark

    def run(self) -> int:
        status = "SUCCESS"
        error_message = None
        row_count = None

        try:
            self.spark = self.build_spark()
            self.logger.info("Starting app=%s run_id=%s", self.config.app_name, self.config.run_id)

            result = self.execute()
            if isinstance(result, int):
                row_count = result

            self.logger.info("Completed app=%s run_id=%s", self.config.app_name, self.config.run_id)
            return 0

        except Exception as exc:
            status = "FAILED"
            error_message = str(exc)
            self.logger.exception("Job failed app=%s run_id=%s", self.config.app_name, self.config.run_id)
            return 1

        finally:
            if self.spark is not None:
                try:
                    AuditLogger(self.spark).log_run(
                        app_name=self.config.app_name,
                        run_id=self.config.run_id,
                        status=status,
                        source_path=self.config.source_path,
                        target_table=self.config.target_table,
                        pipeline_version=self.config.pipeline_version,
                        row_count=row_count,
                        error_message=error_message,
                        audit_table=self.config.ops_run_audit_table,
                        audit_namespace=self.config.ops_namespace,
                    )
                except Exception:
                    self.logger.exception("Failed to write audit log")

                self.spark.stop()

    @abstractmethod
    def execute(self) -> Optional[int]:
        raise NotImplementedError