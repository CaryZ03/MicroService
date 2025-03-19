from flask import Blueprint, jsonify, request
from job.services.product_service import ProductService

product_blueprint = Blueprint('product', __name__)

@product_blueprint.route('/products', methods=['POST'])
def create_product():
    data = request.get_json()
    product = ProductService.create_product(data['name'], data['price'])
    return jsonify(product.to_dict()), 201

@product_blueprint.route('/products/<int:product_id>', methods=['GET'])
def get_product(product_id):
    product = ProductService.get_product(product_id)
    return jsonify(product.to_dict()), 200

@product_blueprint.route('/products', methods=['GET'])
def get_all_products():
    products = ProductService.get_all_products()
    return jsonify([product.to_dict() for product in products]), 200