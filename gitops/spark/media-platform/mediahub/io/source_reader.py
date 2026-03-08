from __future__ import annotations

from pyspark.sql import DataFrame, SparkSession


class SourceReader:
    def __init__(self, spark: SparkSession):
        self.spark = spark

    def read_binary_inventory(
        self,
        source_path: str,
        recursive_lookup: bool = True,
        path_glob_filter: str | None = None,
    ) -> DataFrame:
        reader = self.spark.read.format("binaryFile")

        if recursive_lookup:
            reader = reader.option("recursiveFileLookup", "true")

        if path_glob_filter:
            reader = reader.option("pathGlobFilter", path_glob_filter)

        return reader.load(source_path)