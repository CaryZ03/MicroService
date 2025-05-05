import requests
from mapper import matchEntry
import json

base_url = "http://localhost:8090"


def send_request(method: str, url: str, **kwargs):
    name = method + ":" + url
    with open("entries_demo1.json", "r") as f:
        entries = json.load(f)
    entry = matchEntry(name, entries)
    with open("entry_partitions.json", "r") as f:
        entry_partitions = json.load(f)
    serviceID = entry_partitions.get(entry, None)
    url = f"http://localhost:185{serviceID:02d}{url}"
    method = method.lower()
    if not hasattr(requests, method):
        raise ValueError(f"Unsupported HTTP method: {method.upper()}")
    func = getattr(requests, method)
    return func(url, **kwargs)
    

def register_user(username, email, password):
    response = requests.post(f"{base_url}/users/register", json={
        "username": username,
        "email": email,
        "password": password
    })
    return response

def get_user_by_username(username):
    response = requests.get(f"{base_url}/users/username/{username}")
    return response

def get_user_by_id(user_id):
    response = requests.get(f"{base_url}/users/id/{user_id}")
    return response

def create_product(name, stock, price, category_id):
    response = requests.post(f"{base_url}/products", json={
        "name": name,
        "stock": stock,
        "price": price,
        "category": {
            "id": category_id
        }
    })
    return response

def get_product_by_name(name):
    response = requests.get(f"{base_url}/products/name/{name}")
    return response

def get_product_by_id(product_id):
    response = requests.get(f"{base_url}/products/id/{product_id}")
    return response

def create_category(name):
    response = requests.post(f"{base_url}/categories", json={
        "name": name
    })
    return response

def get_category_by_name(name):
    response = requests.get(f"{base_url}/categories/name/{name}")
    return response

def create_order(user_id, product_id):
    status = "unpaid"
    response = requests.post(f"http://localhost:18503/orders", json={
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
    response = requests.get(f"{base_url}/orders/id/{order_id}")
    return response

def create_order_detail(total_price, order_id, quantity):
    response = requests.post(f"{base_url}/order-details", json={
        "totalPrice": total_price,
        "orderId": order_id,
        "quantity": quantity
    })
    return response

def create_payment_record(order_id, price, status):
    response = requests.post(f"{base_url}/payment-records", json={
        "orderId": order_id,
        "price": price,
        "status": status
    })
    return response



def test():
    username = "name1"
    email = "test@example.com"
    password = "password123"
    response = register_user(username, email, password)
    print("Register User Response:", response.json())
    id = response.json().get("id")
    get_user_by_username(username)
    get_user_by_id(id)
    category_name = "testCategory4"
    response = create_category(category_name)
    print("Create Category Response:", response.json())
    category_id = response.json().get("id")
    product1_name = "testProduct1"
    product1_stock = 10
    product1_price = 100.0
    product2_name = "testProduct2"
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
    get_order_by_id(order_id)
    create_order_detail(100.0, order_id, 2)
    create_order_detail(200.0, order_id, 1)
    create_payment_record(order_id, 100.0, "paid")
    

if __name__ == "__main__":
    # response = requests.get(f"{base_url}/start/test")
    # print("Start Test Response:", response.text)
    test() 
    # response = create_order(1, 1)
    # print("Create Order Response:", response.json())

