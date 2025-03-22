import networkx as nx
from typing import List, Dict, Tuple
import numpy as np

class GraphEnvironment:
    def __init__(self, graph: nx.Graph):
        self.graph = graph
        self.nodes = list(graph.nodes())
        self.num_nodes = len(self.nodes)
        self.current_node_index = 0
        self.node_id_to_index = {node: idx for idx, node in enumerate(self.nodes)}
        self.current_node = self.nodes[self.current_node_index]
        
        # 初始化所有节点的类别为 -1（未分类）
        self.node_categories = {node: -1 for node in self.nodes}
        
        # 初始化节点特征
        self.node_features = {}
        for node in self.nodes:
            if 'feature' in graph.nodes[node]:
                self.node_features[node] = np.array(graph.nodes[node]['feature'], dtype=np.float32)
            else:
                self.node_features[node] = np.random.rand(10).astype(np.float32)
        
        # 构建邻接表
        self.adjacency_list = {node: [] for node in self.nodes}
        for u, v in graph.edges():
            self.adjacency_list[u].append(v)
            self.adjacency_list[v].append(u)
            
    def reset(self) -> np.ndarray:
        self.current_node_index = 0
        self.current_node = self.nodes[self.current_node_index]
        # 重新初始化所有节点的类别为 -1
        self.node_categories = {node: -1 for node in self.nodes}
        return self.get_state(self.current_node)
    
    def get_state(self, node: str) -> np.ndarray:
        return self.node_features[node]
    
    def step(self, action: int) -> Tuple[np.ndarray, float, bool, str]:
        if action == 0:
            new_category: int = max(self.node_categories.values(), default=0) + 1
            self.node_categories[self.current_node] = new_category
        else:
            self.node_categories[self.current_node] = action
        
        self.current_node_index += 1
        if self.current_node_index >= self.num_nodes:
            done: bool = True
            reward: float = self._calculate_final_reward()
            next_node: str = ""
        else:
            done: bool = False
            reward: float = self._calculate_step_reward()
            self.current_node = self.nodes[self.current_node_index]
            next_node: str = self.current_node
        
        next_state: np.ndarray = self.get_state(next_node) if not done else np.zeros_like(self.get_state(self.current_node))
        return next_state, reward, done, next_node
    
    def _calculate_step_reward(self) -> float:
        current_category: int = self.node_categories.get(self.current_node, -1)
        reward: float = 0.0
        neighbors: List[str] = self.adjacency_list.get(self.current_node, [])
        
        # 邻居类别一致性奖励（考虑边的方向）
        for neighbor in neighbors:
            neighbor_category: int = self.node_categories.get(neighbor, -1)
            if neighbor_category == current_category and current_category != -1:
                reward += 0.2
            else:
                reward -= 0.5
        
        # 基于模块化度量的奖励
        modularity_contribution: float = self._calculate_modularity_contribution()
        reward += modularity_contribution * 0.5
        
        # 类别平衡奖励
        category_sizes: Dict[int, int] = {}
        for node, cat in self.node_categories.items():
            if cat != -1:
                category_sizes[cat] = category_sizes.get(cat, 0) + 1
        max_size: int = max(category_sizes.values()) if category_sizes else 0
        avg_size: float = max_size / len(category_sizes) if category_sizes else 0
        if current_category != -1 and category_sizes.get(current_category, 0) > avg_size * 1.5:
            reward -= 1  # 处罚过大类别
        
        # 鼓励形成多个类别
        if current_category == max(self.node_categories.values()):
            reward -= 1.0
        
        # 处罚类别数量过少
        if len(set(self.node_categories.values())) < 2:
            reward -= 1.0
        
        # 处罚类别数量过多
        if len(set(self.node_categories.values())) > 10:
            reward -= 0.5
        
        # 奖励类别内部的有向边
        for u, v in self.graph.edges():
            category_u: int = self.node_categories.get(u, -1)
            category_v: int = self.node_categories.get(v, -1)
            if category_u == category_v and category_u != -1:
                reward += 0.15
        
        return reward
    
    def _calculate_modularity_contribution(self) -> float:
        """
        计算当前分类对模块化度量的贡献。
        """
        edges: List[Tuple[str, str]] = list(self.graph.edges())
        total_edges: int = len(edges)
        if total_edges == 0:
            return 0.0
        
        # 计算类别内部边数
        internal_edges: int = 0
        for u, v in edges:
            if self.node_categories.get(u, -1) == self.node_categories.get(v, -1) and self.node_categories.get(u, -1) != -1:
                internal_edges += 1
        
        # 模块化度量
        modularity: float = internal_edges / total_edges - ((len(set(self.node_categories.values())) - 1) / total_edges)
        return modularity
    
    def _calculate_final_reward(self) -> float:
        total_reward: float = 0.0
        num_communities: int = len(set(self.node_categories.values()))
        
        # 基于类别内部边数计算奖励
        for u, v in self.graph.edges():
            if self.node_categories.get(u, -1) == self.node_categories.get(v, -1) and self.node_categories.get(u, -1) != -1:
                total_reward += 1.0
        
        # 基于模块化度量的奖励
        modularity: float = self._calculate_modularity_contribution()
        total_reward += modularity * 2.0
        
        # 鼓励适当数量的类别
        if num_communities < 2:
            total_reward -= 10.0  # 处罚类别数量过少
        elif num_communities > 15:
            total_reward -= 5.0  # 处罚类别数量过多
        
        return total_reward