from __future__ import annotations

from pyspark.sql import DataFrame
from pyspark.sql.functions import (
    col,
    current_timestamp,
    lit,
    min as spark_min,
    max as spark_max,
    first,
)

# This is a placeholder "silver normalize" step.
# In a real system, ffprobe/mediainfo extraction would happen upstream
# and be merged here.


def transform_bronze_to_silver(bronze_df: DataFrame, current_run_id: str, pipeline_version: str) -> DataFrame:
    return (
        bronze_df.groupBy(
            "file_path",
            "file_name",
            "file_extension",
            "media_domain",
            "content_category",
            "source_system",
            "file_fingerprint",
        )
        .agg(
            first("file_size_bytes").alias("file_size_bytes"),
            first("file_modified_ts").alias("file_modified_ts"),
            spark_min("ingest_ts").alias("first_seen_ts"),
            spark_max("ingest_ts").alias("last_seen_ts"),
        )
        .withColumn("pipeline_version", lit(pipeline_version))
        .withColumn("latest_run_id", lit(current_run_id))
        .withColumn("is_active", lit(True))
    )