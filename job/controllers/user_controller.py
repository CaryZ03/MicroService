from flask import Blueprint, jsonify, request
from job.services.user_service import UserService

user_blueprint = Blueprint('user', __name__)


@user_blueprint.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    user = UserService.create_user(data['username'], data['email'])
    return jsonify(user.to_dict()), 201


@user_blueprint.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = UserService.get_user(user_id)
    return jsonify(user.to_dict()), 200


@user_blueprint.route('/users', methods=['GET'])
def get_all_users():
    users = UserService.get_all_users()
    return jsonify([user.to_dict() for user in users]), 200
