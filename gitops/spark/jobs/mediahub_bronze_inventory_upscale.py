import sys

from mediahub.config import JobConfig
from mediahub.jobs.bronze_inventory_upscale_job import BronzeInventoryUpscaleJob


def main() -> int:
    config = JobConfig.from_env(default_app_name="mediahub-bronze-inventory-upscale")
    return BronzeInventoryUpscaleJob(config).run()


if __name__ == "__main__":
    sys.exit(main())
