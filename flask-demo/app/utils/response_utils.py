# app/utils/response_utils.py
def success_response(data=None, message="Success"):
    return {"data": data, "message": message}, 200

def error_response(message="Error", status_code=400):
    return {"message": message}, status_code