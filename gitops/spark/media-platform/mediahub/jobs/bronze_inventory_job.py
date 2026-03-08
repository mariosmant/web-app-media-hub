from __future__ import annotations

from mediahub.spark_app import SparkJob
from mediahub.io.source_reader import SourceReader
from mediahub.io.iceberg_writer import IcebergWriter
from mediahub.ops.quality import DataQuality
from mediahub.transforms.bronze_inventory import transform_to_inventory


class BronzeInventoryJob(SparkJob):
    def execute(self) -> int:
        assert self.spark is not None

        reader = SourceReader(self.spark)
        writer = IcebergWriter(self.spark)

        writer.create_namespace_if_missing(self.config.bronze_namespace)
        writer.create_bronze_media_table_if_missing(self.config.bronze_table)
        writer.set_write_order(self.config.bronze_table, ["media_domain", "file_path"])

        raw_df = reader.read_binary_inventory(
            source_path=self.config.source_path,
            recursive_lookup=self.config.recursive_lookup,
        )

        discovered_rows = raw_df.count()
        self.logger.info("Discovered rows: %s", discovered_rows)

        if discovered_rows == 0:
            self.logger.warning("No files found at source path: %s", self.config.source_path)
            return 0

        inventory_df = transform_to_inventory(
            df=raw_df,
            source_path=self.config.source_path,
            source_system=self.config.source_system,
            pipeline_version=self.config.pipeline_version,
            run_id=self.config.run_id,
        )

        DataQuality.require_columns(
            inventory_df,
            [
                "file_path",
                "file_name",
                "file_extension",
                "media_domain",
                "file_size_bytes",
                "file_modified_ts",
                "file_fingerprint",
            ],
        )
        DataQuality.assert_non_negative(inventory_df, "file_size_bytes")

        inventory_df.createOrReplaceTempView(self.config.staging_view)

        merge_sql = f"""
        MERGE INTO {self.config.bronze_table} AS t
        USING {self.config.staging_view} AS s
          ON t.file_path = s.file_path
        WHEN MATCHED AND (
             t.file_fingerprint <> s.file_fingerprint
          OR t.file_size_bytes <> s.file_size_bytes
          OR t.file_modified_ts <> s.file_modified_ts
        ) THEN UPDATE SET
          t.file_name = s.file_name,
          t.file_extension = s.file_extension,
          t.media_domain = s.media_domain,
          t.content_category = s.content_category,
          t.file_size_bytes = s.file_size_bytes,
          t.file_modified_ts = s.file_modified_ts,
          t.source_path = s.source_path,
          t.source_system = s.source_system,
          t.pipeline_version = s.pipeline_version,
          t.run_id = s.run_id,
          t.ingest_ts = s.ingest_ts,
          t.ingest_date = s.ingest_date,
          t.file_fingerprint = s.file_fingerprint
        WHEN NOT MATCHED THEN INSERT (
          file_path,
          file_name,
          file_extension,
          media_domain,
          content_category,
          file_size_bytes,
          file_modified_ts,
          source_path,
          source_system,
          pipeline_version,
          run_id,
          ingest_ts,
          ingest_date,
          file_fingerprint
        ) VALUES (
          s.file_path,
          s.file_name,
          s.file_extension,
          s.media_domain,
          s.content_category,
          s.file_size_bytes,
          s.file_modified_ts,
          s.source_path,
          s.source_system,
          s.pipeline_version,
          s.run_id,
          s.ingest_ts,
          s.ingest_date,
          s.file_fingerprint
        )
        """

        writer.merge_from_view(self.config.bronze_table, self.config.staging_view, merge_sql)

        output_rows = self.spark.table(self.config.bronze_table).count()
        self.logger.info("Bronze table row count after merge: %s", output_rows)

        return output_rows