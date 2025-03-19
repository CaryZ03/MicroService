from flask import Flask
from job.utils.database import db, init_db
from job.controllers.user_controller import user_blueprint
from job.controllers.order_controller import order_blueprint
from job.controllers.product_controller import product_blueprint
from job.config import Config

app = Flask(__name__)
app.config.from_object(Config)

# 初始化数据库
init_db(app)

# 注册蓝图
app.register_blueprint(user_blueprint, url_prefix='/api')
app.register_blueprint(order_blueprint, url_prefix='/api')
app.register_blueprint(product_blueprint, url_prefix='/api')


def main(args=None):
    with app.app_context():
        db.create_all()  # 创建数据库表
    app.run(debug=True)


if __name__ == '__main__':
    main()
