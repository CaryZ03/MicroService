# app/services/comment_service.py
from ..models.comment import Comment

def create_comment(data):
    comment = Comment(user_id=data['user_id'], product_id=data['product_id'], content=data['content'])
    comment.save()
    return comment.to_dict()

def get_comment(comment_id):
    comment = Comment.query.get(comment_id)
    return comment.to_dict() if comment else {'message': 'Comment not found'}, 404

def update_comment(comment_id, data):
    comment = Comment.query.get(comment_id)
    if comment:
        comment.content = data.get('content', comment.content)
        comment.save()
        return comment.to_dict()
    return {'message': 'Comment not found'}, 404

def delete_comment(comment_id):
    comment = Comment.query.get(comment_id)
    if comment:
        comment.delete()
        return {'message': 'Comment deleted successfully'}
    return {'message': 'Comment not found'}, 404