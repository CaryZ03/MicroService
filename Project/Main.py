from asyncio import sleep
from typing import List, Dict, Set, Tuple

from tools.GraphMLHelper import GraphMLHelper
from tools.LouvainHelper import LouvainHelper

from dqns.DQNAgent import DQNAgent
from dqns.GraphEnvironment import GraphEnvironment

import os
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

# before we start:
# 1. install everything in requirements.txt.
# 2. GraphMLHelper is a class that helps to read, visiualize and save graphML files.
# 3. LouvainHelper is a class that helps to partition the graph into microservices using Louvain algorithm.


# stage1: I didn't use the DQN model in this stage,
# but Louvain algorithm to partition the graph into microservices.
def stage1Main() -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLSourcePath: str = "./data/src"
    graphMLTargetPath: str = "./data/target"
    # I assume we have many .graphml files in the source path.
    graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    # make partition for each .graphml file.
    for graphMLPath in graphMLPaths:
        graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)
        # visualize the original graph.
        gmh.visualizeGraph(graph, f"{graphMLTargetPath}/origin_{os.path.basename(graphMLPath)}")

        # get microservice partition.
        partition: Dict[str, int] = louvain.partitionCommunities(graph) 
        microservices: Dict[int, List[str]] = louvain.partitionMicroservices(partition)

        # print the partition of microservice.
        for serviceID, functions in microservices.items():
            print(f"microserive {serviceID}: {functions}")

        # convert the microservice partition to nx.DiGraph.
        microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservices)

        # visualize the handled graph.
        gmh.visualizeGraph(microserviceGraph, f"{graphMLTargetPath}/microservice_{os.path.basename(graphMLPath)}")

        # save the microservice graph to a .graphml file.
        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice_{graphMLRelativePath}")

# stage2: use the DQN model.
def stage2Main() -> None:
    gmh: GraphMLHelper = GraphMLHelper()
    louvain: LouvainHelper = LouvainHelper()

    graphMLSourcePath: str = "./data/src"
    graphMLTargetPath: str = "./data/target"
    graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    for graphMLPath in graphMLPaths:
        graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)
        # we use the result of Louvain algorithm as our reference.
        partition_reference: Dict[str, int] = louvain.partitionCommunities(graph)
        microservice_reference: Dict[int, List[str]] = louvain.partitionMicroservices(partition_reference)

        # initialize the environment and agent.
        microservice_count: int = len(microservice_reference.keys())
        env: GraphEnvironment = GraphEnvironment(graph, microservice_count)

        state_dim: int = env.getNodeCnt()
        action_dim: int = env.getNodeCnt() * microservice_count
        agent: DQNAgent = DQNAgent(state_dim, action_dim)

        episodes = 2500
        max_steps = 100
        batch_size = 128

        for episode in range(episodes):
            env.reset()
            
            # the state is a Dict, and we have to convert it into a numpy array.
            ori_state: Dict[int, int] = env.getState()
            state: np.ndarray = np.array(list(ori_state.values()))

            total_reward: float = 0.0
            done: bool = False
            step_count = 0
            
            while not done and step_count < max_steps:
                # get the best action.
                action: int = agent.act(state)

                # get the result of the actino.
                res: Tuple[Dict[int, int], float, bool] = env.step(action)
                ori_next_state: Dict[int, int] = res[0]
                next_state: np.ndarray = np.array(list(ori_next_state.values()))
                reward: float = res[1]
                done: bool = res[2]

                # save to experience replay area.
                agent.add_to_memory(state, action, reward, next_state, done)

                if len(agent.memory) >= batch_size:
                    agent.train(batch_size)

                total_reward += reward
                state = next_state
                step_count += 1
            
            # update the target network evert 50 episodes.
            if episode % 50 == 0:
                    agent.softUpdateTargetNetwork()
            
            if episode % 100 == 0:
                print(f"Episode {episode}, Total Reward: {total_reward:.2f}")

        # ============================
        # print the partition of microservice.
        # ============================
        print("\n=== Final Microservice Partitioning Result ===")

        # change the state into a Dict: {microserviceID: functionName}.
        node_microservice: Dict[int, int] = env.getState()
        microservice_to_nodes: Dict[int, List[str]] = {}
        for node_id, ms_id in node_microservice.items():
            node_name = env.nodes[node_id].name
            microservice_to_nodes.setdefault(ms_id, []).append(node_name)

        for ms_id, node_list in microservice_to_nodes.items():
            print(f"Microservice {ms_id}: Nodes {node_list}")

        # save the partition result.
        microserviceGraph : nx.DiGraph = louvain.convertMicroservicesToGraph(graph, microservice_to_nodes)
        graphMLRelativePath: str = os.path.relpath(graphMLPath, start = graphMLSourcePath)
        gmh.saveGraphAsGraphML(microserviceGraph, f"{graphMLTargetPath}/microservice_{graphMLRelativePath}")

        
if __name__ == "__main__":
    # stage1Main()
    stage2Main()

    # gmh: GraphMLHelper = GraphMLHelper()
    # graphMLSourcePath: str = "./data/src"
    # graphMLTargetPath: str = "./data/target"
    # graphMLPaths: List[str] = gmh.getGraphMLPath(graphMLSourcePath)

    # for graphMLPath in graphMLPaths:
    #     graph : nx.DiGraph = gmh.getGraphFromGraphML(graphMLPath)
    #     for node_name, data in graph.nodes(data=True):
    #         if "Mysql" in node_name:
    #             print("node name: ", node_name, " data: ", data)
        
    #     for u, v, data in graph.edges(data=True):
    #         print("edge: ", u, "->", v, " weight: ", data['weight'])