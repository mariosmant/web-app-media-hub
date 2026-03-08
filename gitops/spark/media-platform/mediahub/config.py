from __future__ import annotations

import os
import uuid
from dataclasses import dataclass
from typing import Optional


def _get_bool(name: str, default: bool) -> bool:
    value = os.getenv(name)
    if value is None:
        return default
    return value.strip().lower() in {"1", "true", "yes", "y", "on"}


def _get_int(name: str, default: int) -> int:
    value = os.getenv(name)
    if value is None:
        return default
    return int(value)


@dataclass(frozen=True)
class JobConfig:
    app_name: str
    source_path: str
    target_table: str
    staging_view: str
    source_system: str
    pipeline_version: str
    recursive_lookup: bool
    debug: bool
    run_id: str

    log_level: str
    shuffle_partitions: int
    adaptive_enabled: bool
    broadcast_timeout: int
    driver_max_result_size: str

    bronze_table: str
    silver_table: str
    gold_table: str
    ops_run_audit_table: str

    checkpoint_path: Optional[str]

    @staticmethod
    def from_env(default_app_name: str) -> "JobConfig":
        return JobConfig(
            app_name=os.getenv("APP_NAME", default_app_name),
            source_path=os.getenv("SOURCE_PATH", "s3a://media-raw/raw/"),
            target_table=os.getenv("TARGET_TABLE", "lakehouse.bronze.media_files"),
            staging_view=os.getenv("STAGING_VIEW", "staged_records"),
            source_system=os.getenv("SOURCE_SYSTEM", "minio"),
            pipeline_version=os.getenv("PIPELINE_VERSION", "v1"),
            recursive_lookup=_get_bool("RECURSIVE_LOOKUP", True),
            debug=_get_bool("DEBUG", False),
            run_id=os.getenv("RUN_ID", str(uuid.uuid4())),
            log_level=os.getenv("LOG_LEVEL", "INFO"),
            shuffle_partitions=_get_int("SPARK_SQL_SHUFFLE_PARTITIONS", 200),
            adaptive_enabled=_get_bool("SPARK_SQL_ADAPTIVE_ENABLED", True),
            broadcast_timeout=_get_int("SPARK_SQL_BROADCAST_TIMEOUT", 1200),
            driver_max_result_size=os.getenv("SPARK_DRIVER_MAX_RESULT_SIZE", "1g"),
            bronze_table=os.getenv("BRONZE_TABLE", "lakehouse.bronze.media_files"),
            silver_table=os.getenv("SILVER_TABLE", "lakehouse.silver.media_assets"),
            gold_table=os.getenv("GOLD_TABLE", "lakehouse.gold.catalog_content"),
            ops_run_audit_table=os.getenv("OPS_RUN_AUDIT_TABLE", "lakehouse.ops.pipeline_runs"),
            checkpoint_path=os.getenv("CHECKPOINT_PATH"),
        )

    @property
    def target_namespace(self) -> str:
        parts = self.target_table.split(".")
        if len(parts) != 3:
            raise ValueError(
                f"target_table must be catalog.namespace.table, got '{self.target_table}'"
            )
        return ".".join(parts[:2])

    @property
    def bronze_namespace(self) -> str:
        return ".".join(self.bronze_table.split(".")[:2])

    @property
    def silver_namespace(self) -> str:
        return ".".join(self.silver_table.split(".")[:2])

    @property
    def gold_namespace(self) -> str:
        return ".".join(self.gold_table.split(".")[:2])

    @property
    def ops_namespace(self) -> str:
        return ".".join(self.ops_run_audit_table.split(".")[:2])