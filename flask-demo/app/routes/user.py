# app/routes/user.py
from flask import Blueprint, request, jsonify
from ..services.user_service import get_user, update_user, delete_user

bp = Blueprint('user', __name__)

@bp.route('/user/<int:user_id>', methods=['GET'])
def get_user_route(user_id):
    return jsonify(get_user(user_id))

@bp.route('/user/<int:user_id>', methods=['PUT'])
def update_user_route(user_id):
    data = request.json
    return jsonify(update_user(user_id, data))

@bp.route('/user/<int:user_id>', methods=['DELETE'])
def delete_user_route(user_id):
    return jsonify(delete_user(user_id))