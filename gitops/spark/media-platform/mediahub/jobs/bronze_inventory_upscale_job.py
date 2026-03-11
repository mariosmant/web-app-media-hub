from __future__ import annotations

import os

from pyspark.sql.functions import col, current_timestamp, lit, lower, regexp_extract, to_date

from mediahub.spark_app import SparkJob
from mediahub.io.source_reader import SourceReader
from mediahub.io.iceberg_writer import IcebergWriter
from mediahub.ops.quality import DataQuality
from mediahub.models import MEDIA_EXTENSION_MAP
from mediahub.transforms.video_upscale import upscale_videos_to_4k


class BronzeInventoryUpscaleJob(SparkJob):
    def execute(self) -> int:
        assert self.spark is not None

        writer = IcebergWriter(self.spark)
        reader = SourceReader(self.spark)
        writer.create_namespace_if_missing(self.config.ops_namespace)
        writer.create_upscale_tracking_table_if_missing(self.config.upscale_tracking_table)

        raw_df = reader.read_binary_inventory(
            source_path=self.config.source_path,
            recursive_lookup=self.config.recursive_lookup,
        )

        video_ext = sorted(MEDIA_EXTENSION_MAP["video"])

        candidates = (
            raw_df
            .select(
                col("path").alias("file_path"),
                regexp_extract(col("path"), r"([^/]+)$", 1).alias("file_name"),
                lower(regexp_extract(col("path"), r"\.([A-Za-z0-9]+)$", 1)).alias("file_extension"),
                col("length").alias("file_size_bytes"),
            )
            .filter(col("file_extension").isin(video_ext))
            .select("file_path", "file_name", "file_size_bytes")
        )

        DataQuality.assert_not_empty(
            candidates,
            message=f"No video rows found for source_path={self.config.source_path}",
        )

        already_done = (
            self.spark.table(self.config.upscale_tracking_table)
            .filter(col("upscale_status").isin("OK", "SKIPPED"))
            .select("source_file_path")
            .withColumnRenamed("source_file_path", "file_path")
            .distinct()
        )

        todo_df = candidates.join(already_done, on="file_path", how="left_anti")

        todo_count = todo_df.count()
        self.logger.info("Videos pending upscale: %s", todo_count)
        if todo_count == 0:
            return 0

        minio_endpoint = self.spark.conf.get("spark.hadoop.fs.s3a.endpoint", None) or os.getenv(
            "MINIO_ENDPOINT", "http://minio:9000"
        )
        minio_access_key = self.spark.conf.get("spark.hadoop.fs.s3a.access.key", None) or os.getenv(
            "MINIO_ACCESS_KEY", "minioadmin"
        )
        minio_secret_key = self.spark.conf.get("spark.hadoop.fs.s3a.secret.key", None) or os.getenv(
            "MINIO_SECRET_KEY", "minioadmin123"
        )

        crf = int(os.getenv("UPSCALE_CRF", "20"))
        preset = os.getenv("UPSCALE_PRESET", "slow")

        result_df = upscale_videos_to_4k(
            todo_df,
            minio_endpoint=minio_endpoint,
            minio_access_key=minio_access_key,
            minio_secret_key=minio_secret_key,
            output_path_or_bucket=self.config.upscale_output_path,
            source_root_path=self.config.source_path,
            crf=crf,
            preset=preset,
        )

        result_df = (
            result_df
            .withColumn("run_id", lit(self.config.run_id))
            .withColumn("pipeline_version", lit(self.config.pipeline_version))
            .withColumn("upscale_ts", current_timestamp())
            .withColumn("upscale_date", to_date(col("upscale_ts")))
        )

        result_df.writeTo(self.config.upscale_tracking_table).append()

        success_count = result_df.filter(col("upscale_status") == "OK").count()
        skipped_count = result_df.filter(col("upscale_status") == "SKIPPED").count()
        failed_count = result_df.filter(col("upscale_status") == "FAILED").count()

        self.logger.info(
            "Upscale completed: ok=%s skipped=%s failed=%s",
            success_count,
            skipped_count,
            failed_count,
        )
        return todo_count
