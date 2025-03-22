from asyncio import sleep
from typing import List, Dict, Set, Tuple

from tools.GraphMLHelper import GraphMLHelper
from tools.LouvainHelper import LouvainHelper

from entities.Node import Node
from entities.MicroService import MicroService

from dqns.DQNetwork import DQNetwork
from dqns.DQNAgent import DQNAgent
from dqns.GraphEnvironment import GraphEnvironment

from community import community_louvain
from community import best_partition

import math
import random
import os
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

# def printMicroService(microServices : Dict[int, MicroService]):
#     for _, microService in microServices.items():
#         microService.printNodes()

# stage1: I didn't use the DQN model in this stage,
# but Louvain algorithm to partition the graph into microservices.
def stage1Main() -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLSourcePath: str = "./data/src"
    graphMLTargetPath: str = "./data/target"
    graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    for graphMLPath in graphMLPaths:
        # get origin graph.
        graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)

        # get partition and microservices.
        partition: Dict[str, int] = louvain.partitionCommunities(graph) 
        microservices: Dict[int, List[str]] = louvain.partitionMicroservices(partition)

        for serviceID, functions in microservices.items():
            print(f"microserive {serviceID}: {functions}")

        microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservices)

        gmh.visualizeGraph(microserviceGraph, "Microservice Graph")

        # graph save.
        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice_{graphMLRelativePath}")

def create_community_graph(env: GraphEnvironment) -> nx.Graph:
    community_graph: nx.Graph = nx.Graph()
    communities: Dict[int, List[str]] = {}
    
    # 按类别分组
    for node, category in env.node_categories.items():
        if category not in communities:
            communities[category] = []
        communities[category].append(node)
    
    # 添加类节点
    for category in communities:
        community_graph.add_node(str(category))
    
    # 添加类之间的边
    added_edges: Set[Tuple[str, str]] = set()
    for u, v in env.graph.edges():
        u_category: int = env.node_categories[u]
        v_category: int = env.node_categories[v]
        u_node: str = str(u_category)
        v_node: str = str(v_category)
        
        if u_node != v_node and (v_node, u_node) not in added_edges:
            community_graph.add_edge(u_node, v_node)
            added_edges.add((u_node, v_node))
    
    return community_graph

def stage2Main() -> None:
    gmh: GraphMLHelper = GraphMLHelper()

    graphMLSourcePath: str = "./data/src"
    graphMLTargetPath: str = "./data/target"
    graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    for graphMLPath in graphMLPaths:
        # get origin graph.
        graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)
        env: GraphEnvironment = GraphEnvironment(graph)

        # 使用 Louvain 算法进行初始分类
        partition: Dict[str, int] = best_partition(graph.to_undirected())
        # 将 Louvain 算法的类别重新映射为连续的整数值
        unique_communities: List[int] = list(set(partition.values()))
        community_mapping: Dict[int, int] = {old: idx for idx, old in enumerate(unique_communities)}
        for node in partition:
            partition[node] = community_mapping[partition[node]]

        # 初始化环境和智能体
        env: GraphEnvironment = GraphEnvironment(graph)
        for node, category in partition.items():
            env.node_categories[node] = category

        state_dim: int = len(env.node_features[next(iter(env.node_features.keys()))])
        action_dim: int = max(partition.values()) + 2  # 增加一个动作用于创建新类别
        agent: DQNAgent = DQNAgent(state_dim, action_dim)

        episodes = 5000
        for episode in range(episodes):
            state: np.ndarray = env.reset()
            total_reward: float = 0.0
            done: bool = False
            
            while not done:
                action: int = agent.act(state, len(env.node_categories.values()))
                next_state, reward, done, _ = env.step(action)
                agent.add_to_memory(state, action, reward, next_state, done)
                total_reward += reward
                state = next_state
                
                if episode % 10 == 0:
                    agent.update_target_network()
            
            if len(agent.memory) >= 32:
                agent.train(batch_size=32)
            
            if episode % 100 == 0:
                print(f"Episode {episode}, Total Reward: {total_reward:.2f}")

        # 测试分类结果
        print("节点分类结果：")
        print(env.node_categories)

        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        resultGraph = create_community_graph(env)

        gmh.visualizeGraph(resultGraph, f"Result Graph {graphMLRelativePath}")
        gmh.saveGraphAsGraphML(resultGraph, f"{graphMLTargetPath}/result_{graphMLRelativePath}")



if __name__ == "__main__":
    # stage1Main()
    stage2Main()