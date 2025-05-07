import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
from sklearn.cluster import KMeans

from read_graph import parse_graphml


class GraphAttentionLayer(nn.Module):
    def __init__(self, in_features, out_features, dropout=0.2, alpha=0.2):
        super(GraphAttentionLayer, self).__init__()
        self.in_features = in_features
        self.out_features = out_features
        self.dropout = dropout
        self.alpha = alpha

        self.W = nn.Parameter(torch.zeros(size=(in_features, out_features)))
        nn.init.xavier_uniform_(self.W.data, gain=1.414)
        self.a = nn.Parameter(torch.zeros(size=(2 * out_features, 1)))
        nn.init.xavier_uniform_(self.a.data, gain=1.414)

        self.leakyrelu = nn.LeakyReLU(self.alpha)

    def forward(self, x, adj):
        h = torch.mm(x, self.W)  # (N, out_features)
        N = h.size(0)

        # 计算注意力系数
        a_input = torch.cat([h.repeat(1, N).view(N * N, -1),
                             h.repeat(N, 1)], dim=1).view(N, -1, 2 * self.out_features)
        e = self.leakyrelu(torch.matmul(a_input, self.a).squeeze(2))

        # 使用邻接矩阵掩码不存在的边
        zero_vec = -9e15 * torch.ones_like(e)
        attention = torch.where(adj > 0, e, zero_vec)
        attention = F.softmax(attention, dim=1)
        attention = F.dropout(attention, self.dropout, training=self.training)

        # 应用注意力
        h_prime = torch.matmul(attention, h)

        # 残差连接
        return F.elu(h_prime + h)


# python
class EnhancedGraphEncoder(nn.Module):
    def __init__(self, input_dim, hidden_dim, embed_dim, num_layers=3, heads=4):
        super(EnhancedGraphEncoder, self).__init__()
        self.num_layers = num_layers
        self.heads = heads

        # Input projection layer
        self.input_proj = nn.Linear(input_dim, hidden_dim)

        # Multi-head attention layers
        self.attentions = nn.ModuleList()
        for _ in range(heads):
            self.attentions.append(GraphAttentionLayer(hidden_dim, hidden_dim))

        # Intermediate layers
        for _ in range(num_layers - 2):
            # These layers expect an input dimension of heads * hidden_dim.
            self.attentions.append(GraphAttentionLayer(hidden_dim * heads, hidden_dim))

        # Output layer : Change input dimension from hidden_dim * heads to hidden_dim.
        self.output_layer = nn.Linear(hidden_dim, embed_dim)

        # Global pooling layer
        self.pool = nn.AdaptiveAvgPool1d(1)

        # Edge prediction head
        self.edge_predictor = nn.Sequential(
            nn.Linear(embed_dim * 2, embed_dim),
            nn.ReLU(),
            nn.Linear(embed_dim, 1),
            nn.Sigmoid()
        )

    def forward(self, x, adj):
        # Input projection
        x = F.relu(self.input_proj(x))

        # Multi-head attention
        attention_outputs = []
        for att in self.attentions[:self.heads]:
            attention_outputs.append(att(x, adj))
        x = torch.cat(attention_outputs, dim=1)

        # Intermediate layers processing using remaining attention layers
        for layer in self.attentions[self.heads:]:
            x = F.relu(layer(x, adj))

        # Obtain node embeddings
        node_embeddings = self.output_layer(x)

        # Obtain graph-level embedding
        graph_embedding = self.pool(node_embeddings.unsqueeze(0).transpose(1, 2)).squeeze()

        # Edge prediction (auxiliary task)
        edge_preds = []
        rows, cols = torch.where(adj > 0)
        for i, j in zip(rows, cols):
            pair = torch.cat([node_embeddings[i], node_embeddings[j]])
            edge_preds.append(self.edge_predictor(pair))
        edge_preds = torch.stack(edge_preds) if edge_preds else torch.tensor([])

        return node_embeddings, graph_embedding, edge_preds


def train_model(model, adj_matrix, epochs=800):
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001, weight_decay=5e-4)

    # 创建单位矩阵作为节点特征
    node_features = torch.eye(adj_matrix.size(0))

    for epoch in range(epochs):
        model.train()
        optimizer.zero_grad()

        # 前向传播
        node_emb, graph_emb, edge_preds = model(node_features, adj_matrix)

        # 主损失: 重构损失
        reconstruction = node_emb @ node_emb.T
        recon_loss = F.mse_loss(reconstruction, adj_matrix)

        # 辅助损失: 边预测损失
        if len(edge_preds) > 0:
            edge_loss = F.binary_cross_entropy(edge_preds.squeeze(),
                                               torch.ones_like(edge_preds.squeeze()))
        else:
            edge_loss = torch.tensor(0.0)

        # 总损失
        total_loss = recon_loss + 0.5 * edge_loss
        total_loss.backward()
        optimizer.step()

        if epoch % 20 == 0:
            print(f'Epoch {epoch}, Loss: {total_loss.item():.4f}')


# 准备数据
call_matrix = np.load("./call_matrix.npy")
file_path = "./demo-with-weight.graphml"
names, numpy_matrix = parse_graphml(file_path)
adj_matrix = torch.FloatTensor(call_matrix)

# 创建并训练模型
model = EnhancedGraphEncoder(
    input_dim=adj_matrix.shape[0],  # 输入维度等于节点数
    hidden_dim=64,  # 减小隐藏层维度
    embed_dim=16,
    num_layers=3,
    heads=4
)

train_model(model, adj_matrix)

# 获取嵌入并进行聚类
with torch.no_grad():
    node_features = torch.eye(adj_matrix.size(0))  # 使用单位矩阵作为节点特征
    node_embeddings, _, _ = model(node_features, adj_matrix)
    embeddings = node_embeddings.numpy()

# 聚类
kmeans = KMeans(n_clusters=40).fit(embeddings)
clusters = {}
for name, label in zip(names, kmeans.labels_):
    clusters.setdefault(label, []).append(name)

# 打印聚类结果
for cluster_id, funcs in clusters.items():
    print(f"Cluster {cluster_id}:")
    print(", ".join(funcs))
    print()
