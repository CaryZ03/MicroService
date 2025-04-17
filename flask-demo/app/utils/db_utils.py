# app/utils/db_utils.py
from .. import db

def save_model(model):
    db.session.add(model)
    db.session.commit()

def delete_model(model):
    db.session.delete(model)
    db.session.commit()