from __future__ import annotations

from mediahub.spark_app import SparkJob
from mediahub.io.iceberg_writer import IcebergWriter
from mediahub.ops.quality import DataQuality
from mediahub.transforms.silver_probe import transform_bronze_to_silver


class SilverProbeJob(SparkJob):
    def execute(self) -> int:
        assert self.spark is not None

        writer = IcebergWriter(self.spark)

        writer.create_namespace_if_missing(self.config.silver_namespace)
        writer.create_silver_media_table_if_missing(self.config.silver_table)
        writer.set_write_order(self.config.silver_table, ["media_domain", "content_category", "file_path"])

        bronze_df = self.spark.table(self.config.bronze_table)

        DataQuality.assert_not_empty(
            bronze_df,
            message=f"Bronze table is empty: {self.config.bronze_table}",
        )

        silver_df = transform_bronze_to_silver(
            bronze_df=bronze_df,
            current_run_id=self.config.run_id,
            pipeline_version=self.config.pipeline_version,
        )

        silver_df.createOrReplaceTempView(self.config.staging_view)

        merge_sql = f"""
        MERGE INTO {self.config.silver_table} AS t
        USING {self.config.staging_view} AS s
          ON t.file_path = s.file_path
        WHEN MATCHED AND (
             t.file_fingerprint <> s.file_fingerprint
          OR t.file_size_bytes <> s.file_size_bytes
          OR t.file_modified_ts <> s.file_modified_ts
          OR t.is_active <> s.is_active
        ) THEN UPDATE SET
          t.file_name = s.file_name,
          t.file_extension = s.file_extension,
          t.media_domain = s.media_domain,
          t.content_category = s.content_category,
          t.file_size_bytes = s.file_size_bytes,
          t.file_modified_ts = s.file_modified_ts,
          t.source_system = s.source_system,
          t.pipeline_version = s.pipeline_version,
          t.latest_run_id = s.latest_run_id,
          t.first_seen_ts = s.first_seen_ts,
          t.last_seen_ts = s.last_seen_ts,
          t.is_active = s.is_active,
          t.file_fingerprint = s.file_fingerprint
        WHEN NOT MATCHED THEN INSERT (
          file_path,
          file_name,
          file_extension,
          media_domain,
          content_category,
          file_size_bytes,
          file_modified_ts,
          source_system,
          pipeline_version,
          latest_run_id,
          first_seen_ts,
          last_seen_ts,
          is_active,
          file_fingerprint
        ) VALUES (
          s.file_path,
          s.file_name,
          s.file_extension,
          s.media_domain,
          s.content_category,
          s.file_size_bytes,
          s.file_modified_ts,
          s.source_system,
          s.pipeline_version,
          s.latest_run_id,
          s.first_seen_ts,
          s.last_seen_ts,
          s.is_active,
          s.file_fingerprint
        )
        """

        writer.merge_from_view(self.config.silver_table, self.config.staging_view, merge_sql)

        output_rows = self.spark.table(self.config.silver_table).count()
        self.logger.info("Silver table row count after merge: %s", output_rows)

        return output_rows