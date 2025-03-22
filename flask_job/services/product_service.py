from flask_job.models.product_model import Product
from flask_job.utils.database import db

class ProductService:
    @staticmethod
    def create_product(name, price):
        product = Product(name=name, price=price)
        db.session.add(product)
        db.session.commit()
        return product

    @staticmethod
    def get_product(product_id):
        return Product.query.get(product_id)

    @staticmethod
    def get_all_products():
        return Product.query.all()