# app/services/order_service.py
from ..models.order import Order

def create_order(data):
    order = Order(user_id=data['user_id'], product_id=data['product_id'], quantity=data['quantity'])
    order.save()
    return order.to_dict()

def get_order(order_id):
    order = Order.query.get(order_id)
    return order.to_dict() if order else {'message': 'Order not found'}, 404

def update_order(order_id, data):
    order = Order.query.get(order_id)
    if order:
        order.quantity = data.get('quantity', order.quantity)
        order.save()
        return order.to_dict()
    return {'message': 'Order not found'}, 404

def delete_order(order_id):
    order = Order.query.get(order_id)
    if order:
        order.delete()
        return {'message': 'Order deleted successfully'}
    return {'message': 'Order not found'}, 404