"""
调用 Flask API 的客户端程序
"""
import requests

# API 基础 URL
BASE_URL = 'http://127.0.0.1:5000/api'


# 1. 创建用户
def create_user(username, email):
    url = f'{BASE_URL}/users'
    data = {'username': username, 'email': email}
    response = requests.post(url, json=data)
    if response.status_code == 201:
        print(f"用户创建成功: {response.json()}")
        return response.json()['id']  # 返回用户 ID
    else:
        print(f"用户创建失败: {response.status_code}, {response.text}")
        return None


# 2. 创建商品
def create_product(name, price):
    url = f'{BASE_URL}/products'
    data = {'name': name, 'price': price}
    response = requests.post(url, json=data)
    if response.status_code == 201:
        print(f"商品创建成功: {response.json()}")
        return response.json()['id']  # 返回商品 ID
    else:
        print(f"商品创建失败: {response.status_code}, {response.text}")
        return None


# 3. 创建订单
def create_order(user_id, product_id, quantity):
    url = f'{BASE_URL}/orders'
    data = {'user_id': user_id, 'product_id': product_id, 'quantity': quantity}
    response = requests.post(url, json=data)
    if response.status_code == 201:
        print(f"订单创建成功: {response.json()}")
        return response.json()['id']  # 返回订单 ID
    else:
        print(f"订单创建失败: {response.status_code}, {response.text}")
        return None


# 4. 获取所有用户
def get_all_users():
    url = f'{BASE_URL}/users'
    response = requests.get(url)
    if response.status_code == 200:
        print("所有用户:")
        for user in response.json():
            print(user)
    else:
        print(f"获取用户失败: {response.status_code}, {response.text}")


# 5. 获取所有商品
def get_all_products():
    url = f'{BASE_URL}/products'
    response = requests.get(url)
    if response.status_code == 200:
        print("所有商品:")
        for product in response.json():
            print(product)
    else:
        print(f"获取商品失败: {response.status_code}, {response.text}")


# 6. 获取所有订单
def get_all_orders():
    url = f'{BASE_URL}/orders'
    response = requests.get(url)
    if response.status_code == 200:
        print("所有订单:")
        for order in response.json():
            print(order)
    else:
        print(f"获取订单失败: {response.status_code}, {response.text}")


# 7. 获取单个用户
def get_user(user_id):
    url = f'{BASE_URL}/users/{user_id}'
    response = requests.get(url)
    if response.status_code == 200:
        print(f"用户信息: {response.json()}")
        return response.json()
    else:
        print(f"获取用户失败: {response.status_code}, {response.text}")
        return None


# 8. 获取单个商品
def get_product(product_id):
    url = f'{BASE_URL}/products/{product_id}'
    response = requests.get(url)
    if response.status_code == 200:
        print(f"商品信息: {response.json()}")
        return response.json()
    else:
        print(f"获取商品失败: {response.status_code}, {response.text}")
        return None


# 9. 获取单个订单
def get_order(order_id):
    url = f'{BASE_URL}/orders/{order_id}'
    response = requests.get(url)
    if response.status_code == 200:
        print(f"订单信息: {response.json()}")
        return response.json()
    else:
        print(f"获取订单失败: {response.status_code}, {response.text}")
        return None


# 主函数
def main():
    # 创建用户
    user_id = create_user('test_user', 'test@example.com')
    if not user_id:
        return

    # 创建商品
    product_id = create_product('test_product', 19.99)
    if not product_id:
        return

    # 创建订单
    order_id = create_order(user_id, product_id, 2)
    if not order_id:
        return

    # 获取所有用户、商品和订单
    get_all_users()
    get_all_products()
    get_all_orders()
    get_order(order_id)
    get_product(product_id)
    get_user(user_id)
    get_all_users()
    get_all_products()
    get_all_orders()
    get_order(order_id)
    get_product(product_id)
    get_user(user_id)


if __name__ == '__main__':
    main()
