from collections import defaultdict

class Graph:
    def __init__(self, vertices):
        self.graph = defaultdict(list)  # 邻接表
        self.vertices = vertices  # 节点列表

    def add_edge(self, u, v):
        self.graph[u].append(v)

    def dfs(self, v, visited, component):
        visited[v] = True
        component.append(v)
        for neighbor in self.graph[v]:
            if not visited[neighbor]:
                self.dfs(neighbor, visited, component)

    def get_weakly_connected_components(self):
        # 将图视为无向图
        undirected_graph = defaultdict(list)
        for u in self.graph:
            for v in self.graph[u]:
                undirected_graph[u].append(v)
                undirected_graph[v].append(u)  # 添加反向边

        visited = {v: False for v in self.vertices}
        components = []

        for v in self.vertices:
            if not visited[v]:
                component = []
                self.dfs(v, visited, component)
                components.append(component)

        return components

# 示例
vertices = [0, 1, 2, 3, 4]
edges = [(0, 1), (1, 2), (3, 4)]

# 构建图
g = Graph(vertices)
for u, v in edges:
    g.add_edge(u, v)

# 获取弱连通分量
weakly_connected_components = g.get_weakly_connected_components()
print("弱连通分量:", weakly_connected_components)