import requests
from mapper import matchEntry
import json

base_url = None


def send_request(method: str, url: str, **kwargs):
    global base_url
    if base_url:
        url = base_url + url
    else:
        name = method + ":" + url
        with open("entries_demo1-origin.json", "r") as f:
            entries = json.load(f)
        entry = matchEntry(name, entries)["name"]
        with open("entry_partitions.json", "r") as f:
            entry_partitions = json.load(f)
        # print(entry_partitions)
        serviceID = entry_partitions.get(entry, None)
        url = f"http://localhost:185{serviceID:02d}{url}"
    method = method.lower()
    if not hasattr(requests, method):
        raise ValueError(f"Unsupported HTTP method: {method.upper()}")
    func = getattr(requests, method)
    return func(url, **kwargs)
    

def register_user(username, email, password):
    response = send_request("POST", f"/users/register", json={
        "username": username,
        "email": email,
        "password": password
    })
    return response

def get_user_by_username(username):
    response = send_request("GET", f"/users/username/{username}")
    return response

def get_user_by_id(user_id):
    response = send_request("GET", f"/users/id/{user_id}")
    return response

def create_product(name, stock, price, category_id):
    response = send_request("POST", f"/products", json={
        "name": name,
        "stock": stock,
        "price": price,
        "category": {
            "id": category_id
        }
    })
    return response

def get_product_by_name(name):
    response = send_request("GET", f"/products/name/{name}")
    return response

def get_product_by_id(product_id):
    response = send_request("GET", f"/products/id/{product_id}")
    return response

def create_category(name):
    response = send_request("POST", f"/categories", json={
        "name": name
    })
    return response

def get_category_by_name(name):
    response = send_request("GET", f"/categories/name/{name}")
    return response

def create_order(user_id, product_id):
    status = "unpaid"
    response = send_request("POST", f"/orders", json={
        "status": status,
        "user": {
            "id": user_id
        },
        "product": {
            "id": product_id
        }
    })
    return response

def get_order_by_id(order_id):
    response = send_request("GET", f"/orders/id/{order_id}")
    return response

def create_order_detail(total_price, order_id, quantity):
    response = send_request("POST", f"/order-details", json={
        "totalPrice": total_price,
        "orderId": order_id,
        "quantity": quantity
    })
    return response

def create_payment_record(order_id, price, status):
    response = send_request("POST", f"/payment-records", json={
        "orderId": order_id,
        "price": price,
        "status": status
    })
    return response



def test(port=None):
    if port:
        global base_url
        base_url = f"http://localhost:{port}"
    username = "name1"
    email = "test@example.com"
    password = "password123"
    response = register_user(username, email, password)
    print("Register User Response:", response.json())
    id = response.json().get("id")
    get_user_by_username(username)
    get_user_by_id(id)
    category_name = "testCategor555"
    response = create_category(category_name)
    print("Create Category Response:", response.json())
    category_id = response.json().get("id")
    product1_name = "testProduct3"
    product1_stock = 10
    product1_price = 100.0
    product2_name = "testProduct4"
    product2_stock = 20
    product2_price = 200.0
    response = create_product(product1_name, product1_stock, product1_price, category_id)
    print("Create Product Response:", response.json())
    product1_id = response.json().get("id")
    response = create_product(product2_name, product2_stock, product2_price, category_id)
    print("Create Product Response:", response.json())
    product2_id = response.json().get("id")
    res = get_category_by_name(category_name)
    print("Get Category Response:", res.json())
    get_product_by_name(product1_name)
    get_product_by_id(product1_id)
    get_product_by_name(product2_name)
    get_product_by_id(product2_id)
    response = create_order(id, product1_id)
    print("Create Order Response:", response.json())
    order_id = response.json().get("id")
    response = create_order_detail(order_id, 100.0, 2)
    print("Create Order Detail Response:", response.json())
    # create_order_detail(200.0, order_id, 1)
    create_payment_record(order_id, 100.0, "Paid")
    response = get_order_by_id(order_id)
    print("Get Order Response:", response.json())
    

if __name__ == "__main__":
    # response = send_request("GET", f"/start/test")
    # print("Start Test Response:", response.text)
    test(8080) 
    # response = create_order(1, 1)
    # print("Create Order Response:", response.json())

