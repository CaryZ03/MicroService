from flask import Blueprint, jsonify
from job.app import Session
from job.app.models import User, Post

main_bp = Blueprint('main', __name__)


# 创建用户
@main_bp.route('/create_user/<username>/<email>', methods=['GET'])
def create_user(username, email):
    session = Session()
    new_user = User(username=username, email=email)
    try:
        session.add(new_user)
        session.commit()
        return jsonify({'message': 'User created successfully'})
    except Exception as e:
        session.rollback()
        return jsonify({'message': f'Error: {str(e)}'})
    finally:
        session.close()


# 创建文章
@main_bp.route('/create_post/<title>/<content>/<user_id>', methods=['GET'])
def create_post(title, content, user_id):
    session = Session()
    user = session.query(User).get(user_id)
    if user:
        new_post = Post(title=title, content=content, user=user)
        try:
            session.add(new_post)
            session.commit()
            return jsonify({'message': 'Post created successfully'})
        except Exception as e:
            session.rollback()
            return jsonify({'message': f'Error: {str(e)}'})
        finally:
            session.close()
    return jsonify({'message': 'User not found'})


# 获取所有用户
@main_bp.route('/users', methods=['GET'])
def get_users():
    session = Session()
    users = session.query(User).all()
    user_list = [{'id': user.id, 'username': user.username, 'email': user.email} for user in users]
    session.close()
    return jsonify(user_list)


# 获取所有文章
@main_bp.route('/posts', methods=['GET'])
def get_posts():
    session = Session()
    posts = session.query(Post).all()
    post_list = [{'id': post.id, 'title': post.title, 'content': post.content, 'user_id': post.user_id} for post in
                 posts]
    session.close()
    return jsonify(post_list)