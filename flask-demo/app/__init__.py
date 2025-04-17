from flask import Flask
from .extensions import db

def create_app():
    app = Flask(__name__)
    app.config.from_object('config.Config')
    
    db.init_app(app)
    
    with app.app_context():
        from .routes import auth, user, product, order, comment, favorite, message
        app.register_blueprint(auth.bp)
        app.register_blueprint(user.bp)
        app.register_blueprint(product.bp)
        app.register_blueprint(order.bp)
        app.register_blueprint(comment.bp)
        app.register_blueprint(favorite.bp)
        app.register_blueprint(message.bp)
        
        db.create_all()
    
    return app