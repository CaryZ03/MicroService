import json
from typing import Dict, Any


def load_config(config_path: str) -> Dict[str, Any]:
    """
    从配置文件中加载配置。
    :param config_path: 配置文件路径。
    :return: 获取到的字典
    """
    with open(config_path, 'r', encoding='utf-8') as f:
        config: Dict[str, Any] = json.load(f)
    return config


class Config:
    """
    Config 类，用于解析配置文件。
    """

    def __init__(self, config_path: str):
        self.config: Dict[str, Any] = load_config(config_path)
