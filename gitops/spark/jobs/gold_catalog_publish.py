import sys

from mediahub.config import JobConfig
from mediahub.jobs.gold_catalog_publish_job import GoldCatalogPublishJob


def main() -> int:
    config = JobConfig.from_env(default_app_name="gold-catalog-publish")
    return GoldCatalogPublishJob(config).run()


if __name__ == "__main__":
    sys.exit(main())