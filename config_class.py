"""
配置文件类，用于加载配置文件中的配置信息。
"""
import json


class Config:
    """
    用于引入配置文件，配置文件中包含了任务类型、任务文件、任务配置等信息。
    """

    def __init__(self, config_path):
        self.config_path = config_path
        self.config = None
        self.load_config()
        self.task_type = self.config['task_type']

    def load_config(self):
        """
        从配置文件中加载配置信息。
        :return: 无
        """
        with open(self.config_path, encoding='utf-8') as file:
            self.config = json.load(file)
