# app/routes/message.py
from flask import Blueprint, request, jsonify
from ..services.message_service import create_message, get_message, delete_message

bp = Blueprint('message', __name__)

@bp.route('/message', methods=['POST'])
def create_message_route():
    data = request.json
    return jsonify(create_message(data))

@bp.route('/message/<int:message_id>', methods=['GET'])
def get_message_route(message_id):
    return jsonify(get_message(message_id))

@bp.route('/message/<int:message_id>', methods=['DELETE'])
def delete_message_route(message_id):
    return jsonify(delete_message(message_id))