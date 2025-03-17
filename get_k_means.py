import numpy as np
import networkx as nx
from node2vec import Node2Vec
from sklearn.cluster import KMeans
from transformers import BertTokenizer, BertModel
import torch

# 示例数据
functions = [
    {
        "function_id": ("calculate_sum", 1, "math_utils.py", 10),
        "call_relations": [("get_numbers", 21, "math_utils.py", 30)],
        "imported_libraries": ["math"],
        "return_names": ["sum_result"],
        "class_name": ["MathUtils"],
        "database_calls": ["math_db"]
    },
    {
        "function_id": ("get_numbers", 21, "math_utils.py", 30),
        "call_relations": [],
        "imported_libraries": [],
        "return_names": ["number_list"],
        "class_name": ["MathUtils"],
        "database_calls": []
    },
    {
        "function_id": ("generate_report", 1, "report_generator.py", 20),
        "call_relations": [("fetch_data", 21, "report_generator.py", 40)],
        "imported_libraries": ["pandas", "matplotlib"],
        "return_names": ["report_file"],
        "class_name": ["ReportGenerator"],
        "database_calls": ["data_db"]
    },
    {
        "function_id": ("fetch_data", 21, "report_generator.py", 40),
        "call_relations": [],
        "imported_libraries": ["sqlite3"],
        "return_names": ["data_frame"],
        "class_name": ["ReportGenerator"],
        "database_calls": ["data_db"]
    },
    {
        "function_id": ("encrypt_data", 1, "security_utils.py", 15),
        "call_relations": [("get_key", 16, "security_utils.py", 25)],
        "imported_libraries": ["cryptography"],
        "return_names": ["encrypted_data"],
        "class_name": ["SecurityUtils"],
        "database_calls": []
    },
    {
        "function_id": ("get_key", 16, "security_utils.py", 25),
        "call_relations": [],
        "imported_libraries": [],
        "return_names": ["encryption_key"],
        "class_name": ["SecurityUtils"],
        "database_calls": []
    },
    {
        "function_id": ("process_image", 1, "image_processor.py", 25),
        "call_relations": [("resize_image", 26, "image_processor.py", 35)],
        "imported_libraries": ["opencv-python"],
        "return_names": ["processed_image"],
        "class_name": ["ImageProcessor"],
        "database_calls": []
    },
    {
        "function_id": ("resize_image", 26, "image_processor.py", 35),
        "call_relations": [],
        "imported_libraries": [],
        "return_names": ["resized_image"],
        "class_name": ["ImageProcessor"],
        "database_calls": []
    },
    {
        "function_id": ("validate_user", 1, "user_auth.py", 18),
        "call_relations": [("check_password", 19, "user_auth.py", 28)],
        "imported_libraries": ["bcrypt"],
        "return_names": ["is_valid"],
        "class_name": ["UserAuth"],
        "database_calls": ["user_db"]
    },
    {
        "function_id": ("check_password", 19, "user_auth.py", 28),
        "call_relations": [],
        "imported_libraries": [],
        "return_names": ["password_match"],
        "class_name": ["UserAuth"],
        "database_calls": []
    },
    {
        "function_id": ("send_email", 1, "email_utils.py", 22),
        "call_relations": [],
        "imported_libraries": ["smtplib"],
        "return_names": ["email_sent"],
        "class_name": ["EmailUtils"],
        "database_calls": []
    },
    {
        "function_id": ("log_event", 1, "logging_utils.py", 16),
        "call_relations": [],
        "imported_libraries": ["logging"],
        "return_names": ["log_status"],
        "class_name": ["LoggingUtils"],
        "database_calls": []
    }
]


# 1. 构建函数描述文本
def build_function_description(func):
    description = f"Function {func['function_id'][0]}"
    if func['class_name']:
        description += f" in class {func['class_name'][0]}"
    if func['return_names']:
        description += f" returns {', '.join(func['return_names'])}"
    if func['imported_libraries']:
        description += f" uses {', '.join(func['imported_libraries'])} libraries"
    if func['database_calls']:
        description += f" and calls databases: {', '.join(func['database_calls'])}"
    func['description'] = func['function_id'][0] if len(func['class_name']) == 0 else func['class_name'][0] + '.' + \
                                                                                      func['function_id'][0]
    return description


descriptions = [build_function_description(func) for func in functions]
print("Function Descriptions:")
for desc in descriptions:
    print(desc)

# 2. 使用 BERT 提取语义特征
bert_base_uncased = './bert_base_uncased'
tokenizer = BertTokenizer.from_pretrained(bert_base_uncased)
model = BertModel.from_pretrained(bert_base_uncased)


def extract_semantic_features(descriptions):
    features = []
    for desc in descriptions:
        inputs = tokenizer(desc, return_tensors='pt', truncation=True, padding=True)
        with torch.no_grad():
            outputs = model(**inputs)
        cls_feature = outputs.last_hidden_state[:, 0, :].numpy()  # 使用 [CLS] token 的特征
        features.append(cls_feature)
    return np.vstack(features)


semantic_features = extract_semantic_features(descriptions)
print("\nSemantic Features Shape:", semantic_features.shape)

# 3. 处理函数调用关系
G = nx.DiGraph()
for func in functions:
    func_id = func['function_id'][0]
    G.add_node(func_id)
    for call in func['call_relations']:
        G.add_edge(func_id, call[0])

# 使用 Node2Vec 提取调用关系特征
node2vec = Node2Vec(G, dimensions=64, walk_length=30, num_walks=200, workers=4)
n2v_model = node2vec.fit(window=10, min_count=1, batch_words=4)

# 获取每个函数的调用关系特征
call_relation_features = np.array([n2v_model.wv[node] for node in G.nodes()])
print("Call Relation Features Shape:", call_relation_features.shape)

# 4. 定义权重
weights = {
    "semantic": 40,  # 语义特征权重
    "call_relation": 30,  # 调用关系特征权重
    "imported_libraries": 5,  # 导入库权重
    "return_names": 5,  # 返回值名称权重
    "database_calls": 20,  # 数据库调用权重
    "name_features": 60  # 函数名称语义
}


def encode_description(desc):
    desc_feature = np.zeros(10)
    for ch in desc:
        # 使用字符的 ASCII 码作为哈希的一部分
        idx = ord(ch) % 10
        desc_feature[idx] += 1
    return desc_feature


# 5. 加权特征融合
def weighted_feature_fusion(functions, semantic_features, call_relation_features, weights):
    # 初始化加权特征列表
    weighted_features = []

    for i, func in enumerate(functions):
        # 语义特征
        semantic_feature = semantic_features[i] * weights["semantic"]

        # 调用关系特征
        call_relation_feature = call_relation_features[i] * weights["call_relation"]

        # 导入库特征
        imported_libs = func['imported_libraries']
        imported_lib_feature = np.zeros(10)  # 假设最多 10 种库
        for lib in imported_libs:
            lib_hash = hash(lib) % 10  # 简单哈希编码
            imported_lib_feature[lib_hash] = 1
        imported_lib_feature = imported_lib_feature * weights["imported_libraries"]

        # 返回值名称特征
        return_names = func['return_names']
        return_name_feature = np.zeros(10)  # 假设最多 10 种返回值
        for name in return_names:
            name_hash = hash(name) % 10  # 简单哈希编码
            return_name_feature[name_hash] = 1
        return_name_feature = return_name_feature * weights["return_names"]

        # 数据库调用特征
        db_calls = func['database_calls']
        db_call_feature = np.zeros(10)  # 假设最多 10 种数据库
        for db in db_calls:
            db_hash = hash(db) % 10  # 简单哈希编码
            db_call_feature[db_hash] = 1
        db_call_feature = db_call_feature * weights["database_calls"]

        description = encode_description(func['description'] * weights['name_features'])

        # 拼接所有加权特征
        combined_feature = np.hstack([
            description,
            semantic_feature,
            call_relation_feature,
            imported_lib_feature,
            return_name_feature,
            db_call_feature,
        ])
        weighted_features.append(combined_feature)

    return np.vstack(weighted_features)


# 加权特征融合
weighted_features = weighted_feature_fusion(functions, semantic_features, call_relation_features, weights)
print("Weighted Features Shape:", weighted_features.shape)

# 6. 聚类
kmeans = KMeans(n_clusters=4)
clusters = kmeans.fit_predict(weighted_features)

# 输出聚类结果
print("\nClustering Results:")
for i, func in enumerate(functions):
    print(f"Function {func['function_id'][0]} is in cluster {clusters[i]}")


# 7. 优化跨聚类调用
def optimize_clusters(functions, clusters, G):
    cluster_map = {func['function_id'][0]: cluster_id for func, cluster_id in zip(functions, clusters)}

    # 检查跨聚类调用
    for edge in G.edges():
        src, dst = edge
        if cluster_map[src] != cluster_map[dst]:
            # 将被调用的函数移动到调用函数的聚类中
            cluster_map[dst] = cluster_map[src]

    # 更新聚类结果
    new_clusters = [cluster_map[func['function_id'][0]] for func in functions]
    return new_clusters

# 优化聚类
# optimized_clusters = optimize_clusters(functions, clusters, G)
# print("\nOptimized Clustering Results:")
# for i, func in enumerate(functions):
#     print(f"Function {func['function_id'][0]} is in cluster {optimized_clusters[i]}")