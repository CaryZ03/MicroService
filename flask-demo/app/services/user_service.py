# app/services/user_service.py
from ..models.user import User
from ..utils.auth_utils import generate_token

def register_user(data):
    user = User(username=data['username'], email=data['email'], password=data['password'])
    user.save()
    return {'message': 'User registered successfully', 'user_id': user.id}

def login_user(data):
    user = User.query.filter_by(username=data['username'], password=data['password']).first()
    if user:
        token = generate_token(user.id)
        return {'message': 'Login successful', 'token': token}
    else:
        return {'message': 'Invalid credentials'}, 401

def get_user(user_id):
    user = User.query.get(user_id)
    return user.to_dict() if user else {'message': 'User not found'}, 404

def update_user(user_id, data):
    user = User.query.get(user_id)
    if user:
        user.username = data.get('username', user.username)
        user.email = data.get('email', user.email)
        user.save()
        return user.to_dict()
    return {'message': 'User not found'}, 404

def delete_user(user_id):
    user = User.query.get(user_id)
    if user:
        user.delete()
        return {'message': 'User deleted successfully'}
    return {'message': 'User not found'}, 404