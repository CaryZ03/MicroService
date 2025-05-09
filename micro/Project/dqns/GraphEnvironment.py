import networkx as nx
import community as community_louvain
import numpy as np
import math

from typing import List, Dict, Tuple, Set

from entities.Node import Node
from entities.Edge import Edge
from entities.MicroService import MicroService

class GraphEnvironment:
    def __init__(self, directed_graph: nx.DiGraph, microservice_count: int):

        # initialize the graph and its nodes.
        self.dir_graph: nx.DiGraph = directed_graph
        self.undir_graph: nx.Graph = directed_graph.to_undirected()

        # we use index to represent the node, since the neuro network can only accept numbers.
        self.nodes: Dict[int, Node] = {}
        self.node_name_to_ind: Dict[str, int] = {}
        self.node_count: int = len(self.dir_graph.nodes)

        self.edges: Dict[int, List[Edge]] = {}

        self.microservices: Dict[int, MicroService] = {}
        self.microservices_count: int = microservice_count
        self.node_microservice: Dict[int, int] = {}

        for i in range(self.microservices_count):
            self.microservices[i] = MicroService(i)


        self.node_call_database: Dict[int, Set[int]] = {}

        self.current_node_index = 0

        # initialize nodes with information.
        ind = 0
        for node_name, data in self.dir_graph.nodes(data=True):
            self.nodes[ind] = Node(node_name, data['execution_time'], data['count'])
            self.node_name_to_ind[node_name] = ind

            self.node_microservice[ind] = ind % self.microservices_count
            self.edges[ind] = []

            ind += 1
        
        # initialize edges with information.
        self.edge_size: int = len(self.dir_graph.edges())
        self.edge_weights: int = 0
        for u, v, data in self.dir_graph.edges(data=True):

            uind: int = self.node_name_to_ind[u]
            vind: int = self.node_name_to_ind[v]

            # print("src node: ", v, " to node: ", u)

            # the .graphml's edges seems to have REVERSE edges !!
            new_edge: Edge = Edge(uind, vind, data['weight'])
            self.edge_weights += data['weight']

            if self.edges.get(uind) is None:
                self.edges[uind] = []
            self.edges[uind].append(new_edge)

            # whether v called database. If v called, then u have one more caller.
            if "Mysql" in v:
                if self.node_call_database.get(vind) is None:
                    self.node_call_database[vind] = set()
                self.node_call_database.get(vind).add(uind)

        print("init graph environment successfully.")
        print("the node count: ", self.node_count)
    
    # reset the environment.
    def reset(self) ->None:
        self.current_node_index = 0
        for i in range(self.node_count):
            self.node_microservice[i] = i % self.microservices_count
    
    def getNodeCnt(self) -> int:
        return self.node_count
    
    def getState(self) -> Dict[int, int]:
        return self.node_microservice
    
    def step(self, action: int) -> Tuple[Dict[int, int], float, bool]:
        func_id, new_service_id = action // self.microservices_count, action % self.microservices_count
        node_microservice_before: Dict[int, int] = self.node_microservice.copy()
        
        self.node_microservice[func_id] = new_service_id
        node_microservice_after: Dict[int, int] = self.node_microservice.copy()
        reward: float = self.calcStepReward(node_microservice_before, node_microservice_after)

        # at present, we think if we have done node_count steps, we finish the task!!
        self.current_node_index += 1

        done: bool = self.current_node_index >= self.node_count
        next_state: Dict[int, int] = node_microservice_after

        return next_state, reward, done

    def calcStepReward(self, node_microservice_before: Dict[int, int],
                               node_microservice_after: Dict[int, int]) -> float:
        
        return self.calcReward(node_microservice_after) - self.calcReward(node_microservice_before) 
    
    def calcReward(self, node_microservice: Dict[int, int]) -> float:
        modularity = self.calcModularity(node_microservice)
        intra_cohesion = self.calcIntraServiceCohesion(node_microservice)
        inter_coupling = self.calcInterServiceCoupling(node_microservice)
        size_balance = self.calcServiceSizeBalance(node_microservice)
        data_consistency = self.calcDataConsistency(node_microservice)

        # print("module: ", modularity, " intra: ", 
        #       intra_cohesion, " inter: ", inter_coupling, " balance: ", size_balance,
        #       " data_cons: ", data_consistency)

        reward = modularity + intra_cohesion - inter_coupling + size_balance + data_consistency
        return reward


    def calcModularity(self, node_microservice: Dict[int, int]) -> float:
        communities = {}
        for node_index, microservice_index in node_microservice.items():
            node_name = self.nodes[node_index].get_node_name()
            communities[node_name] = microservice_index

        return community_louvain.modularity(communities, self.undir_graph) * self.node_count
    
    def calcIntraServiceCohesion(self, node_microservice: Dict[int, int]) -> float:
        intra_edges = 0
        # 假设这个边权有意义的话
        for u, edges in self.edges.items():
            for edge in edges:
                if node_microservice.get(u) == node_microservice.get(edge.getTo()):
                    intra_edges += edge.getWeight()
        # normalize to [0, 1]
        return intra_edges / self.edge_weights

    def calcInterServiceCoupling(self, node_microservice: Dict[int, int]) -> float:
        inter_coupling = 0
        for u, edges in self.edges.items():
            for edge in edges:
                if node_microservice.get(u) != node_microservice.get(edge.getTo()):
                    inter_coupling += edge.getWeight()
        # normalize to [0, 1]
        return inter_coupling / self.edge_weights

    def calcServiceSizeBalance(self, node_microservice: Dict[int, int]) -> float:
        microservice_sizes = {}
        for _, microservice_index in node_microservice.items():
            microservice_sizes[microservice_index] = microservice_sizes.get(microservice_index, 0) + 1
        # derivation.
        size_std = np.std(list(microservice_sizes.values()))
        return 1 / (1 + size_std)
    
    def calcDataConsistency(self, node_microservice: Dict[int, int]) -> float:
        data_consistency : float = 0.0
        for db_node_index, caller_node_indexes in self.node_call_database.items():
            db_microservice_index: int = node_microservice[db_node_index]
            data_cur_consistency: float = 0.0

            for caller_node_index in caller_node_indexes:
                caller_microservice_index: int = node_microservice[caller_node_index]
                data_cur_consistency += db_microservice_index == caller_microservice_index
            
            data_cur_consistency /= len(caller_node_indexes)
            data_consistency += data_cur_consistency
        
        data_consistency /= len(self.node_call_database)
        
        return data_consistency