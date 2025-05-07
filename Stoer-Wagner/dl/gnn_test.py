import torch
import torch.nn as nn
import numpy as np
from sklearn.cluster import KMeans

# 输入数据
sim_matrix = np.load("../results/dependence/mono2micro_matrix.npy")  # 假设已加载相似度矩阵
names = [
    {
        "func_name": "Config",
        "file_path": "examples/flask_demo/config",
        "lineno": 25,
        "end_lineno": 28,
        "execution_time": 0,
        "count": 0,
        "label": 5
    },
    {
        "func_name": "Product",
        "file_path": "examples/flask_demo/models/product_model",
        "lineno": 7,
        "end_lineno": 27,
        "execution_time": 0,
        "count": 1,
        "label": 2
    },
    {
        "func_name": "OrderService",
        "file_path": "examples/flask_demo/services/order_service",
        "lineno": 10,
        "end_lineno": 29,
        "execution_time": 89,
        "count": 5,
        "label": 1
    },
    {
        "func_name": "main",
        "file_path": "examples/flask_demo/app",
        "lineno": 33,
        "end_lineno": 38,
        "execution_time": 54409,
        "count": 2,
        "label": 7
    },
    {
        "func_name": "UserController",
        "file_path": "examples/flask_demo/controllers/user_controller",
        "lineno": 7,
        "end_lineno": 30,
        "execution_time": 116,
        "count": 5,
        "label": 0
    },
    {
        "func_name": "patch_trace",
        "file_path": "examples/flask_demo/patch_trace",
        "lineno": 10,
        "end_lineno": 63,
        "execution_time": 0,
        "count": 0,
        "label": 6
    },
    {
        "func_name": "ProductController",
        "file_path": "examples/flask_demo/controllers/product_controller",
        "lineno": 7,
        "end_lineno": 30,
        "execution_time": 104,
        "count": 5,
        "label": 2
    },
    {
        "func_name": "User",
        "file_path": "examples/flask_demo/models/user_model",
        "lineno": 6,
        "end_lineno": 22,
        "execution_time": 0,
        "count": 1,
        "label": 0
    },
    {
        "func_name": "testcase",
        "file_path": "examples/flask_demo/tmp",
        "lineno": 4,
        "end_lineno": 6,
        "execution_time": 0,
        "count": 8,
        "label": 8
    },
    {
        "func_name": "ProductService",
        "file_path": "examples/flask_demo/services/product_service",
        "lineno": 10,
        "end_lineno": 28,
        "execution_time": 90,
        "count": 5,
        "label": 2
    },
    {
        "func_name": "Weight",
        "file_path": "examples/flask_demo/controllers/order_controller",
        "lineno": 8,
        "end_lineno": 9,
        "execution_time": 0,
        "count": 0,
        "label": 4
    },
    {
        "func_name": "OrderController",
        "file_path": "examples/flask_demo/controllers/order_controller",
        "lineno": 12,
        "end_lineno": 35,
        "execution_time": 102,
        "count": 5,
        "label": 1
    },
    {
        "func_name": "DatabaseManager",
        "file_path": "examples/flask_demo/utils/database",
        "lineno": 7,
        "end_lineno": 14,
        "execution_time": 2,
        "count": 2,
        "label": 3
    },
    {
        "func_name": "Order",
        "file_path": "examples/flask_demo/models/order_model",
        "lineno": 7,
        "end_lineno": 28,
        "execution_time": 0,
        "count": 1,
        "label": 1
    },
    {
        "func_name": "UserService",
        "file_path": "examples/flask_demo/services/user_service",
        "lineno": 10,
        "end_lineno": 28,
        "execution_time": 100,
        "count": 5,
        "label": 0
    }
] # 元素名称列表


# 1. 构建GNN模型（简单图自编码器）
class GraphEncoder(nn.Module):
    def __init__(self, input_dim, hidden_dim, embed_dim):
        super().__init__()
        self.fc1 = nn.Linear(input_dim, hidden_dim)
        self.fc2 = nn.Linear(hidden_dim, embed_dim)

    def forward(self, adj_matrix):
        x = torch.relu(self.fc1(adj_matrix))
        return self.fc2(x)


# 2. 训练模型
model = GraphEncoder(input_dim=len(names), hidden_dim=64, embed_dim=16)
optimizer = torch.optim.Adam(model.parameters())
adj_matrix = torch.FloatTensor(sim_matrix)  # 相似度矩阵转为邻接矩阵

for epoch in range(100):
    embeddings = model(adj_matrix)
    loss = torch.norm(adj_matrix - embeddings @ embeddings.T)  # 重构损失
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()

# 3. 聚类嵌入向量
embeddings = model(adj_matrix).detach().numpy()
kmeans = KMeans(n_clusters=4).fit(embeddings)
clusters = {}
for name, label in zip(names, kmeans.labels_):
    clusters.setdefault(label, []).append(name['func_name'])
print(clusters)