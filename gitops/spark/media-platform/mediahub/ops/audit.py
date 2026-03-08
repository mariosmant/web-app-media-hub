from __future__ import annotations

from pyspark.sql import SparkSession
from pyspark.sql.functions import current_timestamp, to_date, lit


class AuditLogger:
    def __init__(self, spark: SparkSession):
        self.spark = spark

    def log_run(
        self,
        app_name: str,
        run_id: str,
        status: str,
        source_path: str,
        target_table: str,
        pipeline_version: str,
        row_count: int | None,
        error_message: str | None,
        audit_table: str,
        audit_namespace: str,
    ) -> None:
        self.spark.sql(f"CREATE NAMESPACE IF NOT EXISTS {audit_namespace}")

        self.spark.sql(f"""
        CREATE TABLE IF NOT EXISTS {audit_table} (
            app_name STRING,
            run_id STRING,
            status STRING,
            source_path STRING,
            target_table STRING,
            pipeline_version STRING,
            row_count BIGINT,
            error_message STRING,
            event_ts TIMESTAMP,
            event_date DATE
        )
        USING iceberg
        PARTITIONED BY (event_date, app_name)
        """)

        df = self.spark.range(1).select(
            lit(app_name).alias("app_name"),
            lit(run_id).alias("run_id"),
            lit(status).alias("status"),
            lit(source_path).alias("source_path"),
            lit(target_table).alias("target_table"),
            lit(pipeline_version).alias("pipeline_version"),
            lit(row_count).cast("bigint").alias("row_count"),
            lit(error_message).alias("error_message"),
            current_timestamp().alias("event_ts"),
        ).withColumn("event_date", to_date("event_ts"))

        df.writeTo(audit_table).append()