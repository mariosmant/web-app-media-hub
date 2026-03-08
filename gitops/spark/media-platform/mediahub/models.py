from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class RunMetrics:
    discovered_rows: int = 0
    output_rows: int = 0
    inserted_rows: Optional[int] = None
    updated_rows: Optional[int] = None


MEDIA_EXTENSION_MAP = {
    "video": {"mp4", "mov", "mkv", "avi", "mxf", "webm", "m4v", "ts"},
    "audio": {"mp3", "wav", "flac", "aac", "ogg", "m4a"},
    "image": {"jpg", "jpeg", "png", "webp", "gif", "tiff", "bmp"},
}