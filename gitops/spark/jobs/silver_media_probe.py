import sys

from mediahub.config import JobConfig
from mediahub.jobs.silver_probe_job import SilverProbeJob


def main() -> int:
    config = JobConfig.from_env(default_app_name="silver-media-probe")
    return SilverProbeJob(config).run()


if __name__ == "__main__":
    sys.exit(main())