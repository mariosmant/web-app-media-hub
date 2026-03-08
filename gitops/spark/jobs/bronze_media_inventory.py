import sys

from mediahub.config import JobConfig
from mediahub.jobs.bronze_inventory_job import BronzeInventoryJob


def main() -> int:
    config = JobConfig.from_env(default_app_name="bronze-media-inventory")
    return BronzeInventoryJob(config).run()


if __name__ == "__main__":
    sys.exit(main())