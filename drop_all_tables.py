import os
from dotenv import load_dotenv
from sqlalchemy import create_engine, MetaData, Table
from sqlalchemy.orm import sessionmaker

# 加载环境变量
load_dotenv()

# 数据库配置
HOSTNAME = os.getenv('DB_HOST', '182.92.100.66')  # 从环境变量读取，默认值为 '182.92.100.66'
DATABASE = os.getenv('DB_NAME', 'autotracer')      # 从环境变量读取，默认值为 'autotracer'
PORT = int(os.getenv('DB_PORT', 3306))            # 从环境变量读取，默认值为 3306
USERNAME = os.getenv('DB_USER', 'autotracer')     # 从环境变量读取，默认值为 'autotracer'
PASSWORD = os.getenv('DB_PASSWORD', 'BpGJmWWDWNn4R6AN')  # 从环境变量读取，默认值为 'BpGJmWWDWNn4R6AN'

# 构建数据库 URI
DATABASE_URI = f'mysql+pymysql://{USERNAME}:{PASSWORD}@{HOSTNAME}:{PORT}/{DATABASE}'

# 创建数据库引擎
engine = create_engine(DATABASE_URI)

# 创建会话
Session = sessionmaker(bind=engine)
session = Session()

# 获取数据库元数据
metadata = MetaData()  # 不传递 bind 参数
metadata.reflect(bind=engine)  # 在 reflect 方法中传递 bind 参数

def delete_all_table_data():
    """
    删除数据库中所有表的内容
    """
    try:
        # 遍历所有表
        for table_name, table in metadata.tables.items():
            print(f"正在删除表 {table_name} 的内容...")
            # 执行删除操作
            session.execute(table.delete())
            session.commit()
        print("所有表的内容已成功删除！")
    except Exception as e:
        session.rollback()  # 回滚事务
        print(f"删除表内容时出错: {e}")
    finally:
        session.close()

if __name__ == '__main__':
    # 确认操作
    delete_all_table_data()