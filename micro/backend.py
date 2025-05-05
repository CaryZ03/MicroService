from flask import Flask, request, jsonify
from flask_cors import CORS
import json

app = Flask(__name__)
CORS(app)  # 允许所有来源的请求


@app.route('/init', methods=['GET'])
def get_partitions():
    with open("vueData.json", "r") as f:
    # with open("partitions_user.json", "r") as f:
        vueData = json.load(f)
    
    return vueData


@app.route('/save', methods=['Post'])
def save_partitions():
    partitions = request.get_json()
    
    with open("partitions_user.json", "w") as f:
        json.dump(partitions, f, indent=4)
    
    return {"message": "Partitions saved successfully!"}


@app.route('/create', methods=['Post'])
def create_microservices():
    partitions = request.get_json()
    
    with open("partitions_user.json", "w") as f:
        json.dump(partitions, f, indent=4)
    
    return {"message": "Partitions saved successfully!"}


if __name__ == "__main__":
    app.run(debug=True)