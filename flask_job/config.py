import os
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

# 项目根目录
BASE_DIR = os.path.abspath(os.path.dirname(__file__))

# 数据库配置
HOSTNAME = os.getenv('DB_HOST', '182.92.100.66')  # 从环境变量读取，默认值为 '182.92.100.66'
DATABASE = os.getenv('DB_NAME', 'autotracer')      # 从环境变量读取，默认值为 'autotracer'
PORT = int(os.getenv('DB_PORT', 3306))            # 从环境变量读取，默认值为 3306
USERNAME = os.getenv('DB_USER', 'autotracer')     # 从环境变量读取，默认值为 'autotracer'
PASSWORD = os.getenv('DB_PASSWORD', 'BpGJmWWDWNn4R6AN')  # 从环境变量读取，默认值为 'BpGJmWWDWNn4R6AN'

# 构建数据库 URI
DATABASE_URI = f'mysql+pymysql://{USERNAME}:{PASSWORD}@{HOSTNAME}:{PORT}/{DATABASE}'

# 日志文件路径
LOG_FILE = os.path.join(BASE_DIR, 'app.log')

# 其他配置
class Config:
    SQLALCHEMY_DATABASE_URI = DATABASE_URI
    SQLALCHEMY_TRACK_MODIFICATIONS = False  # 禁用 SQLAlchemy 的修改跟踪
    LOG_FILE = LOG_FILE