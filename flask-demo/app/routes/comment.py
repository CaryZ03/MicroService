# app/routes/comment.py
from flask import Blueprint, request, jsonify
from ..services.comment_service import create_comment, get_comment, update_comment, delete_comment

bp = Blueprint('comment', __name__)

@bp.route('/comment', methods=['POST'])
def create_comment_route():
    data = request.json
    return jsonify(create_comment(data))

@bp.route('/comment/<int:comment_id>', methods=['GET'])
def get_comment_route(comment_id):
    return jsonify(get_comment(comment_id))

@bp.route('/comment/<int:comment_id>', methods=['PUT'])
def update_comment_route(comment_id):
    data = request.json
    return jsonify(update_comment(comment_id, data))

@bp.route('/comment/<int:comment_id>', methods=['DELETE'])
def delete_comment_route(comment_id):
    return jsonify(delete_comment(comment_id))