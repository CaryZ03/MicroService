import json

source = "./partitions-demo1-cpu.json"
target = source.split(".json")[0] + "-xht.json"

with open(source, 'r', encoding='utf-8') as f:
    data = json.load(f)

result = {}

for key, value in data.items():
    if value not in result.keys():
        result[value] = []
    if key.startswith("com"):
        result[value].append(key)


# 按照result中key的字典序排序
result = dict(sorted(result.items(), key=lambda item: item[0]))

print(result)

with open(target, 'w', encoding='utf-8') as f:
    json.dump(result, f, indent=4)