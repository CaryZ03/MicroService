import os

# 项目根目录
BASE_DIR = os.path.abspath(os.path.dirname(__file__))

# 数据库配置
HOSTNAME = '182.92.100.66'
DATABASE = 'autotracer'
PORT = 3306
USERNAME = 'autotracer'
PASSWORD = 'BpGJmWWDWNn4R6AN'
DATABASE_URI = 'mysql+pymysql://{}:{}@{}:{}/{}'.format(USERNAME, PASSWORD, HOSTNAME, PORT, DATABASE)