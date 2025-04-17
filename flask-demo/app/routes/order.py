# app/routes/order.py
from flask import Blueprint, request, jsonify
from ..services.order_service import create_order, get_order, update_order, delete_order

bp = Blueprint('order', __name__)

@bp.route('/order', methods=['POST'])
def create_order_route():
    data = request.json
    return jsonify(create_order(data))

@bp.route('/order/<int:order_id>', methods=['GET'])
def get_order_route(order_id):
    return jsonify(get_order(order_id))

@bp.route('/order/<int:order_id>', methods=['PUT'])
def update_order_route(order_id):
    data = request.json
    return jsonify(update_order(order_id, data))

@bp.route('/order/<int:order_id>', methods=['DELETE'])
def delete_order_route(order_id):
    return jsonify(delete_order(order_id))