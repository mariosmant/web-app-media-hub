from __future__ import annotations

from pyspark.sql import DataFrame
from pyspark.sql.functions import (
    col,
    concat_ws,
    current_timestamp,
    lit,
    lower,
    regexp_extract,
    sha2,
    to_date,
    when,
)

from mediahub.models import MEDIA_EXTENSION_MAP


def transform_to_inventory(df: DataFrame, source_path: str, source_system: str, pipeline_version: str, run_id: str) -> DataFrame:
    video_ext = sorted(MEDIA_EXTENSION_MAP["video"])
    audio_ext = sorted(MEDIA_EXTENSION_MAP["audio"])
    image_ext = sorted(MEDIA_EXTENSION_MAP["image"])

    result = (
        df.select(
            col("path").alias("file_path"),
            regexp_extract(col("path"), r"([^/]+)$", 1).alias("file_name"),
            lower(regexp_extract(col("path"), r"\.([A-Za-z0-9]+)$", 1)).alias("file_extension"),
            col("length").alias("file_size_bytes"),
            col("modificationTime").alias("file_modified_ts"),
        )
        .withColumn(
            "media_domain",
            when(col("file_extension").isin(video_ext), lit("video"))
            .when(col("file_extension").isin(audio_ext), lit("audio"))
            .when(col("file_extension").isin(image_ext), lit("image"))
            .otherwise(lit("other"))
        )
        .withColumn(
            "content_category",
            when(col("file_path").contains("/movie/"), lit("movie"))
            .when(col("file_path").contains("/series/"), lit("series"))
            .when(col("file_path").contains("/episode/"), lit("episode"))
            .when(col("file_path").contains("/short/"), lit("short"))
            .when(col("file_path").contains("/music-video/"), lit("music-video"))
            .when(col("file_path").contains("/gallery/"), lit("gallery"))
            .when(col("file_path").contains("/poster/"), lit("poster"))
            .when(col("file_path").contains("/track/"), lit("track"))
            .otherwise(lit("unknown"))
        )
        .withColumn("source_path", lit(source_path))
        .withColumn("source_system", lit(source_system))
        .withColumn("pipeline_version", lit(pipeline_version))
        .withColumn("run_id", lit(run_id))
        .withColumn("ingest_ts", current_timestamp())
        .withColumn("ingest_date", to_date(col("ingest_ts")))
        .withColumn(
            "file_fingerprint",
            sha2(
                concat_ws(
                    "||",
                    col("file_path"),
                    col("file_size_bytes").cast("string"),
                    col("file_modified_ts").cast("string"),
                ),
                256,
            ),
        )
        .dropDuplicates(["file_path", "file_fingerprint"])
    )

    return result