import logging

LOG_FORMATTER = '%(asctime)s - %(name)s - %(levelname)s - %(lineno)d  - %(message)s'

def build_logger(log_file: str = None, class_name:str = None) -> logging.Logger:
    logger = logging.getLogger(class_name)
    logger.setLevel(logging.DEBUG)
    if log_file:
        handler = logging.FileHandler(log_file)
    handler = logging.StreamHandler()
    formatter = logging.Formatter(LOG_FORMATTER)
    handler.setFormatter(formatter)
    logger.addHandler(handler)
    return logger
