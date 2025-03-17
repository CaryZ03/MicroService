from flask import Flask
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from job.config import DATABASE_URI

app = Flask(__name__)

# 创建数据库引擎
engine = create_engine(DATABASE_URI)
# 创建会话工厂
Session = sessionmaker(bind=engine)

# 导入路由
from job.app.routes import main_bp
app.register_blueprint(main_bp)