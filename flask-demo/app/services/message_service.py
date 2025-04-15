# app/services/message_service.py
from ..models.message import Message

def create_message(data):
    message = Message(sender_id=data['sender_id'], receiver_id=data['receiver_id'], content=data['content'])
    message.save()
    return message.to_dict()

def get_message(message_id):
    message = Message.query.get(message_id)
    return message.to_dict() if message else {'message': 'Message not found'}, 404

def delete_message(message_id):
    message = Message.query.get(message_id)
    if message:
        message.delete()
        return {'message': 'Message deleted successfully'}
    return {'message': 'Message not found'}, 404