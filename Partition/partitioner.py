from asyncio import sleep
from typing import List, Dict, Set, Tuple

from .tools.GraphMLHelper import GraphMLHelper
from .tools.LouvainHelper import LouvainHelper

from .entities.Node import Node
from .entities.MicroService import MicroService

from .dqns.DQNetwork import DQNetwork
from .dqns.DQNAgent import DQNAgent
from .dqns.GraphEnvironment import GraphEnvironment

import community
from community import community_louvain
from community import best_partition

import math
import random
import os
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

# stage1: I didn't use the DQN model in this stage,
# but Louvain algorithm to partition the graph into microservices.
def stage1Main(G: nx.DiGraph) -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLTargetPath: str = "Partition/data/target"

    # get origin graph.
    graph : nx.DiGraph = G

    # get partition and microservices.
    partition: Dict[str, int] = louvain.partitionCommunities(graph) 
    microservices: Dict[int, List[str]] = louvain.partitionMicroservices(partition)

    for serviceID, functions in microservices.items():
        print(f"microserive {serviceID}: {functions}")

    microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservices)

    # graph save.
    gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice.graphml")

# stage2: use the DQN model.
def stage2Main(G: nx.DiGraph) -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLTargetPath: str = "Partition/data/target"

    # get origin graph.
    graph : nx.DiGraph = G
    env: GraphEnvironment = GraphEnvironment(graph)

    # KEY: the number of microservice and node are equal at the beginning.
    # The state would record the category of each node,
    # the action represents move a mode from current microservice to another.
    state_dim: int = env.get_node_cnt()
    action_dim: int = pow(env.get_node_cnt(), 2)
    agent: DQNAgent = DQNAgent(state_dim = state_dim, action_dim = action_dim)

    episodes = 3000
    for episode in range(episodes):
        env.reset()
        # the state from env is DICT!!!
        ori_state: Dict[int, int] = env.get_state()
        state: np.ndarray = np.array(list(ori_state))
        total_reward: float = 0.0
        done: bool = False
        
        while not done:
            action: int = agent.act(state)
            res: Tuple[Dict[int, int], float, bool, str] = env.step(action)
            # the state from env is DICT!!!
            ori_next_state: Dict[int, int] = res[0]
            next_state: np.ndarray = np.array(list(ori_next_state))
            reward: float = res[1]
            done: bool = res[2]

            agent.add_to_memory(state, action, reward, next_state, done)

            total_reward += reward
            env.update_best_partition(total_reward, ori_next_state)

            state = next_state
            
            if episode % 10 == 0:
                agent.update_target_network()
        
        if len(agent.memory) >= 32:
            agent.train(batch_size=32)
        
        if episode % 100 == 0:
            print(f"Episode {episode}, Total Reward: {total_reward:.2f}")

    # ============================
    # print the partition of microservice.
    # ============================
    print("\n=== Final Microservice Partitioning Result ===")
    node_microservice: Dict[int, int] = env.get_best_partition()

    microservice_to_nodes: Dict[int, List[str]] = {}
    for node_id, ms_id in node_microservice.items():
        node_name = env.nodes[node_id].name
        microservice_to_nodes.setdefault(ms_id, []).append(node_name)

    for ms_id, node_list in microservice_to_nodes.items():
        print(f"Microservice {ms_id}: Nodes {node_list}")

    microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservice_to_nodes)
    gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice")
