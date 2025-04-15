# app/services/favorite_service.py
from ..models.favorite import Favorite

def create_favorite(data):
    favorite = Favorite(user_id=data['user_id'], product_id=data['product_id'])
    favorite.save()
    return favorite.to_dict()

def get_favorite(favorite_id):
    favorite = Favorite.query.get(favorite_id)
    return favorite.to_dict() if favorite else {'message': 'Favorite not found'}, 404

def delete_favorite(favorite_id):
    favorite = Favorite.query.get(favorite_id)
    if favorite:
        favorite.delete()
        return {'message': 'Favorite deleted successfully'}
    return {'message': 'Favorite not found'}, 404