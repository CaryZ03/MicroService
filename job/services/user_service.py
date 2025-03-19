from job.models.user_model import User
from job.utils.database import db
from job.tmp import testcase

class UserService:
    @staticmethod
    def create_user(username, email):
        user = User(username=username, email=email)
        db.session.add(user)
        db.session.commit()
        return user

    @staticmethod
    def get_user(user_id):
        return User.query.get(user_id)

    @staticmethod
    def get_all_users():
        testcase()
        return User.query.all()