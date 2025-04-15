from flask import Blueprint, request, jsonify
from ..services.user_service import register_user, login_user

bp = Blueprint('auth', __name__)

@bp.route('/register', methods=['POST'])
def register():
    data = request.json
    result = register_user(data)
    return jsonify(result)

@bp.route('/login', methods=['POST'])
def login():
    data = request.json
    result = login_user(data)
    return jsonify(result)