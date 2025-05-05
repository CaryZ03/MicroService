from asyncio import sleep
from typing import List, Dict, Set, Tuple

from tools.GraphMLHelper import GraphMLHelper
from tools.LouvainHelper import LouvainHelper

from entities.Node import Node
from entities.MicroService import MicroService

from dqns.DQNetwork import DQNetwork
from dqns.DQNAgent import DQNAgent
from dqns.GraphEnvironment import GraphEnvironment

import community
from community import community_louvain
from community import best_partition

import math
import random
import os
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt
import json

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
        print(partition)
        microservices: Dict[int, List[str]] = louvain.partitionMicroservices(partition)

        with open("partition.json", "w") as f:
            json.dump(partition, f, indent=4)

        microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservices)

        # graph save.
        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice_{graphMLRelativePath}")
        
        return partition

# stage2: use the DQN model.
def stage2Main() -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLSourcePath: str = "./data/src"
    graphMLTargetPath: str = "./data/target"
    graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    for graphMLPath in graphMLPaths:
        # get origin graph.
        graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)
        env: GraphEnvironment = GraphEnvironment(graph)

        state_dim: int = env.getNodeCnt()
        microservice_count: int = int(math.sqrt(env.getNodeCnt()))
        action_dim: int = env.getNodeCnt() * microservice_count
        agent: DQNAgent = DQNAgent(state_dim, action_dim)

        episodes = 5000
        max_steps = 100
        batch_size = 32

        for episode in range(episodes):
            env.reset()
            # the state from env is DICT!!!
            ori_state: Dict[int, int] = env.getState()
            state: np.ndarray = np.array(list(ori_state.values()))

            total_reward: float = 0.0
            done: bool = False
            step_count = 0
            
            while not done and step_count < max_steps:
                action: int = agent.act(state)
                # print("action: ", action)
                res: Tuple[Dict[int, int], float, bool] = env.step(action)
                # the state from env is DICT!!!
                ori_next_state: Dict[int, int] = res[0]
                next_state: np.ndarray = np.array(list(ori_next_state.values()))
                reward: float = res[1]
                done: bool = res[2]

                agent.add_to_memory(state, action, reward, next_state, done)

                if len(agent.memory) >= batch_size:
                    agent.train(batch_size)

                total_reward += reward
                state = next_state
                step_count += 1

            if episode % 10 == 0:
                    agent.softUpdateTargetNetwork()
            
            if episode % 100 == 0:
                print(f"Episode {episode}, Total Reward: {total_reward:.2f}")

        # ============================
        # print the partition of microservice.
        # ============================
        print("\n=== Final Microservice Partitioning Result ===")
        node_microservice: Dict[int, int] = env.getState()

        microservice_to_nodes: Dict[int, List[str]] = {}
        for node_id, ms_id in node_microservice.items():
            node_name = env.nodes[node_id].name
            microservice_to_nodes.setdefault(ms_id, []).append(node_name)

        for ms_id, node_list in microservice_to_nodes.items():
            print(f"Microservice {ms_id}: Nodes {node_list}")
            
        with open("microservices1.json", "w") as f:
            json.dump(microservice_to_nodes, f, indent=4)

        microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservice_to_nodes)
        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice_{graphMLRelativePath}")

        
if __name__ == "__main__":
    # stage1Main()
    stage2Main()