from __future__ import annotations

from pyspark.sql.functions import current_timestamp, col, count, sum as spark_sum, max as spark_max

from mediahub.spark_app import SparkJob
from mediahub.io.iceberg_writer import IcebergWriter
from mediahub.ops.quality import DataQuality


class GoldCatalogPublishJob(SparkJob):
    def execute(self) -> int:
        assert self.spark is not None

        writer = IcebergWriter(self.spark)

        writer.create_namespace_if_missing(self.config.gold_namespace)
        writer.create_gold_catalog_table_if_missing(self.config.gold_table)
        writer.set_write_order(self.config.gold_table, ["media_domain", "content_category"])

        silver_df = self.spark.table(self.config.silver_table)

        DataQuality.assert_not_empty(
            silver_df,
            message=f"Silver table is empty: {self.config.silver_table}",
        )

        gold_df = (
            silver_df
            .filter(col("is_active") == True)  # noqa: E712
            .groupBy("media_domain", "content_category")
            .agg(
                count("*").alias("total_assets"),
                spark_sum("file_size_bytes").alias("total_bytes"),
                spark_max("last_seen_ts").alias("latest_ingest_ts"),
            )
            .withColumn("publish_ts", current_timestamp())
        )

        gold_df.createOrReplaceTempView(self.config.staging_view)

        writer.overwrite_from_view(
            self.config.gold_table,
            f"""
            SELECT
                media_domain,
                content_category,
                total_assets,
                total_bytes,
                latest_ingest_ts,
                publish_ts
            FROM {self.config.staging_view}
            """,
        )

        output_rows = self.spark.table(self.config.gold_table).count()
        self.logger.info("Gold table row count after publish: %s", output_rows)

        return output_rows