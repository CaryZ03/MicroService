from mapper import buildGraph, showGraph
import networkx as nx
import time
import requests
import shutil
import subprocess

import os
import sys
import json
from test import test

source = "D:/Programs/MicroService/micro/demos/demo1-origin"
target = source + "-fuxiiiii"
proj_name = source.split("/")[-1]
print(proj_name)

# source = target

G = nx.DiGraph()

def reconstruct(partitions, output):
    # pass
    utils = "D:/Programs/MicroService/call-graph/tools/src/main/java/com/micro/test/utils"
    main = source + "/src/main/java/com/example/demo"
    
    shutil.copytree(utils, main, dirs_exist_ok=True)
    print(f"文件夹 '{utils}' 已成功复制为 '{main}'。")

    # with open("Partition/microservices.json", "r") as f:
    #     data = json.load(f)
    # with open("Partition/partition.json", "r") as f:
    #     partition = json.load(f)
    
    data = output

    # dest_folder = "D:/Programs/MicroService/call-graph/demo-micro"
    # dest_folder = "D:/Programs/MicroService/call-graph/tools/src/main/java/com/micro/test/demo"
    dest_folder = source + "-micro1"
    

    for key, value in data.items():
        # print(f"Key: {key}, Value: {value}")
        target = dest_folder + "/" + proj_name + str(key)

        shutil.copytree(source, target, dirs_exist_ok=True)
        # print(f"文件夹 '{source}' 已成功复制为 '{target}'。")
        
        json_data = {
            "target_path": target,
            **value,
            "partition": partitions,
            "port": f"185{int(key):02d}",
        }
        
        # print(json_data)

        response = requests.post("http://127.0.0.1:18555/reconstruct", json=json_data)
        print(response.text)
        # break


    # response = requests.get("http://127.0.0.1:18555/test")
    # print(response.text)


def save_microservices(partitions, G):
    microservices = {}

    for node, communityID in partitions.items():
        if communityID not in microservices:
            microservices[communityID] = []
        microservices[communityID].append(node)
    
    output = {}
    
    entry_partitions = {}

    # 假设 microservices 是一个字典，格式为 {serviceID: [func1, func2, ...]}
    # 假设 graph 是通过 nx.DiGraph 加载的有向图
    # 假设 output 是一个字典，用于记录结果

    for serviceID, functions in microservices.items():
        print(f"microservice {serviceID}: {functions}")
        output[serviceID] = {}
        output[serviceID]["functions"] = functions
        output[serviceID]["ins"] = []
        output[serviceID]["outs"] = []
        for func in functions:
            # 查找所有在 graph 中指向 func 节点的边
            predecessors = list(G.predecessors(func))  # 获取所有指向 func 的节点
            for predecessor in predecessors:
                if predecessor not in partitions:
                    if G.nodes[predecessor]["type"] == "entry":
                        entry_partitions[predecessor] = serviceID
                    continue
                in_serviceID = partitions[predecessor]
                if in_serviceID != serviceID:
                    if func not in output[serviceID]["ins"]:
                        output[serviceID]["ins"].append(func)
            successors = list(G.successors(func))  # 获取所有指向 func 的节点
            for successor in successors:
                if successor not in partitions:
                    continue
                out_serviceID = partitions[successor]
                if out_serviceID != serviceID:
                    if func not in output[serviceID]["outs"]:
                        output[serviceID]["outs"].append(successor)
        
    with open("microservices.json", "w") as f:
        json.dump(output, f, indent=4)
        
    with open("entry_partitions.json", "w") as f:
        json.dump(entry_partitions, f, indent=4)
        
    return output


def main():
    G = nx.read_graphml("Partition/data/src/graph.graphml")
    
    with open("partitions_user.json", "r") as f:
        partitions = json.load(f)
    
    output = save_microservices(partitions, G)
    
    # save_to_json(partitions, G)
    
    # reconstruct(partitions, output)
    

main()
