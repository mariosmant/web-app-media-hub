from __future__ import annotations

from pyspark.sql import SparkSession


class IcebergWriter:
    def __init__(self, spark: SparkSession):
        self.spark = spark

    def create_namespace_if_missing(self, namespace: str) -> None:
        self.spark.sql(f"CREATE NAMESPACE IF NOT EXISTS {namespace}")

    def create_bronze_media_table_if_missing(self, full_table_name: str) -> None:
        self.spark.sql(f"""
        CREATE TABLE IF NOT EXISTS {full_table_name} (
            file_path STRING,
            file_name STRING,
            file_extension STRING,
            media_domain STRING,
            content_category STRING,
            file_size_bytes BIGINT,
            file_modified_ts TIMESTAMP,
            source_path STRING,
            source_system STRING,
            pipeline_version STRING,
            run_id STRING,
            ingest_ts TIMESTAMP,
            ingest_date DATE,
            file_fingerprint STRING
        )
        USING iceberg
        PARTITIONED BY (ingest_date, media_domain)
        """)

    def create_silver_media_table_if_missing(self, full_table_name: str) -> None:
        self.spark.sql(f"""
        CREATE TABLE IF NOT EXISTS {full_table_name} (
            file_path STRING,
            file_name STRING,
            file_extension STRING,
            media_domain STRING,
            content_category STRING,
            file_size_bytes BIGINT,
            file_modified_ts TIMESTAMP,
            source_system STRING,
            pipeline_version STRING,
            latest_run_id STRING,
            first_seen_ts TIMESTAMP,
            last_seen_ts TIMESTAMP,
            is_active BOOLEAN,
            file_fingerprint STRING
        )
        USING iceberg
        PARTITIONED BY (media_domain, content_category)
        """)

    def create_gold_catalog_table_if_missing(self, full_table_name: str) -> None:
        self.spark.sql(f"""
        CREATE TABLE IF NOT EXISTS {full_table_name} (
            media_domain STRING,
            content_category STRING,
            total_assets BIGINT,
            total_bytes BIGINT,
            latest_ingest_ts TIMESTAMP,
            publish_ts TIMESTAMP
        )
        USING iceberg
        PARTITIONED BY (media_domain, content_category)
        """)

    def create_pipeline_audit_table_if_missing(self, full_table_name: str) -> None:
        self.spark.sql(f"""
        CREATE TABLE IF NOT EXISTS {full_table_name} (
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

    def set_write_order(self, full_table_name: str, columns: list[str]) -> None:
        joined = ", ".join(columns)
        self.spark.sql(f"ALTER TABLE {full_table_name} WRITE ORDERED BY ({joined})")

    def merge_from_view(self, full_table_name: str, staged_view: str, merge_sql: str) -> None:
        self.spark.sql(merge_sql)

    def overwrite_from_view(self, full_table_name: str, select_sql: str) -> None:
        self.spark.sql(f"INSERT OVERWRITE {full_table_name} {select_sql}")