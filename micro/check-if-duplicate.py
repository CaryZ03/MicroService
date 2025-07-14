import json

with open('./microservice-partitions.json', 'r', encoding='utf-8') as f:
    partitions = json.load(f)

all_methods = []

for service, methods in partitions.items():
    for method in methods:
        if all_methods.count(method) != 0:
            print("Duplicate method found:", method)
        all_methods.append(method)
