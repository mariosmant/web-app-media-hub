# import logging
# import sys
#
#
# def setup_logging(log_level: str = "INFO") -> logging.Logger:
#     logging.basicConfig(
#         level=getattr(logging, log_level.upper(), logging.INFO),
#         format="%(asctime)s %(levelname)s %(name)s %(message)s",
#         stream=sys.stdout,
#         force=True,
#     )
#     return logging.getLogger("mediahub")
#
import logging

def setup_logging(log_level: str = "INFO") -> logging.Logger:
    logger = logging.getLogger("mediahub")
    logger.setLevel(getattr(logging, log_level.upper(), logging.INFO))
    return logger