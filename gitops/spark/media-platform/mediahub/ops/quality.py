from __future__ import annotations

from pyspark.sql import DataFrame


class DataQuality:
    @staticmethod
    def require_columns(df: DataFrame, required_columns: list[str]) -> None:
        missing = [c for c in required_columns if c not in df.columns]
        if missing:
            raise ValueError(f"Missing required columns: {missing}")

    @staticmethod
    def assert_non_negative(df: DataFrame, column_name: str) -> None:
        count = df.filter(f"{column_name} < 0").limit(1).count()
        if count > 0:
            raise ValueError(f"Column '{column_name}' contains negative values")

    @staticmethod
    def assert_not_empty(df: DataFrame, message: str = "DataFrame is empty") -> None:
        if df.limit(1).count() == 0:
            raise ValueError(message)