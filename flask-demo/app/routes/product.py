# app/routes/product.py
from flask import Blueprint, request, jsonify
from ..services.product_service import create_product, get_product, update_product, delete_product

bp = Blueprint('product', __name__)

@bp.route('/product', methods=['POST'])
def create_product_route():
    data = request.json
    return jsonify(create_product(data))

@bp.route('/product/<int:product_id>', methods=['GET'])
def get_product_route(product_id):
    return jsonify(get_product(product_id))

@bp.route('/product/<int:product_id>', methods=['PUT'])
def update_product_route(product_id):
    data = request.json
    return jsonify(update_product(product_id, data))

@bp.route('/product/<int:product_id>', methods=['DELETE'])
def delete_product_route(product_id):
    return jsonify(delete_product(product_id))