# app/routes/favorite.py
from flask import Blueprint, request, jsonify
from ..services.favorite_service import create_favorite, get_favorite, delete_favorite

bp = Blueprint('favorite', __name__)

@bp.route('/favorite', methods=['POST'])
def create_favorite_route():
    data = request.json
    return jsonify(create_favorite(data))

@bp.route('/favorite/<int:favorite_id>', methods=['GET'])
def get_favorite_route(favorite_id):
    return jsonify(get_favorite(favorite_id))

@bp.route('/favorite/<int:favorite_id>', methods=['DELETE'])
def delete_favorite_route(favorite_id):
    return jsonify(delete_favorite(favorite_id))