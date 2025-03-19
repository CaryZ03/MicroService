from flask_sqlalchemy import SQLAlchemy
from job.config import Config

db = SQLAlchemy()

def init_db(app):
    app.config['SQLALCHEMY_DATABASE_URI'] = Config.SQLALCHEMY_DATABASE_URI
    db.init_app(app)