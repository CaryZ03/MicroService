import networkx as nx
import community as community_louvain
import math

from typing import List, Dict, Tuple

from entities.Node import Node
from entities.MicroService import MicroService

class GraphEnvironment:
    def __init__(self, graph: nx.Graph):

        # initialize the graph and its nodes.
        self.graph: nx.DiGraph = graph
        self.nodes: Dict[int, Node] = {}
        self.microservices: Dict[int, MicroService] = {}

        self.current_node_index = 0
        self.node_microservice: Dict[int, int] = {}

        # at the beginning, each node belongs to one microservice.
        ind = -1
        for node_name, data in self.graph.nodes(data=True):
            ind += 1
            self.nodes[ind] = Node(node_name, data['execution_time'], data['count'])
            self.microservices[ind] = MicroService(ind)
            self.node_microservice[ind] = ind

        self.node_count = ind + 1

        print("init graph environment successfully.")
        print("the node count: ", self.node_count)

        self.best_reward: float = -math.inf
        self.best_partition: Dict[int, int] = {}
            
    def reset(self) ->None:
        self.current_node_index = 0
        for i in range(self.node_count):
            self.node_microservice[i] = i
    
    def get_node_cnt(self) -> int:
        return self.node_count
    
    def get_state(self) -> Dict[int, int]:
        return self.node_microservice
    
    def step(self, action: int) -> Tuple[Dict[int, int], float, bool, str]:
        # func that we are going to move to target_service
        func_id, target_service = action // self.node_count, action % self.node_count
        
        # the origin node microservice dict.
        node_microservice_before: Dict[int, int] = self.node_microservice.copy()
        self.node_microservice[func_id] = target_service
        # the updated node microservice dict.
        node_microservice_after: Dict[int, int] = self.node_microservice.copy()

        reward: float = self._calculate_step_reward(node_microservice_before, node_microservice_after)

        # learning the greatest microservice node by node.
        self.current_node_index += 1

        done: bool = self.current_node_index >= self.node_count
        next_node: str = self.nodes[self.current_node_index] if not done else "NONE"
        next_state: Dict[int, int] = node_microservice_after

        return next_state, reward, done, next_node

    def _calculate_step_reward(self, node_microservice_before: Dict[int, int],
                               node_microservice_after: Dict[int, int]) -> float:
        reward = 0.0

        cross_edges_before = self._count_cross_service_edges(node_microservice_before)
        modularity_before = self._calculate_modularity(node_microservice_before)

        cross_edges_after = self._count_cross_service_edges(node_microservice_after)
        modularity_after = self._calculate_modularity(node_microservice_after)

        delta_cross = cross_edges_before - cross_edges_after
        delta_modularity = modularity_after - modularity_before
        # print("delta modularity: ", delta_modularity)

        reward += delta_cross * 1.0
        reward += delta_modularity * 10000.0

        # Balance reward
        # category_sizes = {}
        # for node, cat in node_microservice_after.items():
        #     category_sizes[cat] = category_sizes.get(cat, 0) + 1

        # avg_size = sum(category_sizes.values()) / len(category_sizes)
        # for size in category_sizes.values():
        #     if size > avg_size * 1.5:
        #         reward -= 1.0

        num_services = len(set(node_microservice_after.values()))
        if num_services < 2:
            reward -= 10.0
        elif num_services > 12:
            reward -= 5.0

        return reward

    def _count_cross_service_edges(self, node_microservice: Dict[int, int]) -> int:
        cross_edges = 0
        for u, v in self.graph.edges():
            if node_microservice.get(u) != node_microservice.get(v):
                cross_edges += 1
        return cross_edges

    def _calculate_modularity(self, node_microservice: Dict[int, int]) -> float:
        G_undirected = self.graph.to_undirected()
        communities = {}
        for node_index, microservice_index in node_microservice.items():
            node_name = self.nodes[node_index].get_node_name()
            communities[node_name] = microservice_index

        return community_louvain.modularity(communities, G_undirected)
    
    def update_best_partition(self, total_reward: float, node_microservice: Dict[int, int]) -> None:
        if total_reward > self.best_reward:
            self.best_reward = total_reward
            self.best_partition = node_microservice

    def get_best_partition(self) -> Dict[int, int]:
        return self.best_partition