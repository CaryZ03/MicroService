from flask import Blueprint, jsonify, request
from flask_job.services.order_service import OrderService

order_blueprint = Blueprint('order', __name__)


@order_blueprint.route('/orders', methods=['POST'])
def create_order():
    data = request.get_json()
    order = OrderService.create_order(data['user_id'], data['product_id'], data['quantity'])
    return jsonify(order.to_dict()), 201


@order_blueprint.route('/orders/<int:order_id>', methods=['GET'])
def get_order(order_id):
    order = OrderService.get_order(order_id)
    return jsonify(order.to_dict()), 200


@order_blueprint.route('/orders', methods=['GET'])
def get_all_orders():
    orders = OrderService.get_all_orders()
    return jsonify([order.to_dict() for order in orders]), 200
