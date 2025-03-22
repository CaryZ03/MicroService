from flask_job.models.order_model import Order
from flask_job.utils.database import db
from flask_job.tmp import testcase

class OrderService:
    @staticmethod
    def create_order(user_id, product_id, quantity):
        order = Order(user_id=user_id, product_id=product_id, quantity=quantity)
        db.session.add(order)
        db.session.commit()
        return order

    @staticmethod
    def get_order(order_id):
        testcase()
        return Order.query.get(order_id)

    @staticmethod
    def get_all_orders():
        testcase()
        return Order.query.all()