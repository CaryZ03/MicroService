# app/services/product_service.py
from ..models.product import Product

def create_product(data):
    product = Product(name=data['name'], description=data['description'], price=data['price'])
    product.save()
    return product.to_dict()

def get_product(product_id):
    product = Product.query.get(product_id)
    return product.to_dict() if product else {'message': 'Product not found'}, 404

def update_product(product_id, data):
    product = Product.query.get(product_id)
    if product:
        product.name = data.get('name', product.name)
        product.description = data.get('description', product.description)
        product.price = data.get('price', product.price)
        product.save()
        return product.to_dict()
    return {'message': 'Product not found'}, 404

def delete_product(product_id):
    product = Product.query.get(product_id)
    if product:
        product.delete()
        return {'message': 'Product deleted successfully'}
    return {'message': 'Product not found'}, 404