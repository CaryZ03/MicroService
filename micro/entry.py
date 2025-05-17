from mapper import buildGraph, showGraph
import networkx as nx
import time
import requests
import shutil
import subprocess

import os
import sys
import json
import glob

from graph import generate_echarts_html
from test import test

# from flask import Flask, request, jsonify
# from flask_cors import CORS

# app = Flask(__name__)
# CORS(app)  # 允许所有来源的请求

# source = "D:/Programs/MicroService/call-graph/demo"
source = "D:/Programs/MicroService/call-graph/tools/demo"


# command = 'java -javaagent:"D:/Programs/MicroService/apache-skywalking-apm-10.2.0/apache-skywalking-apm-bin/agent/skywalking-agent.jar" -D"skywalking.agent.service_name=micro-dev::micro-system" -D"skywalking.collector.backend_service=127.0.0.1:11800" -jar target/demo1-0.0.1-SNAPSHOT.jar'
# java -javaagent:"D:/Programs/MicroService/apache-skywalking-apm-10.2.0/apache-skywalking-apm-bin/agent/skywalking-agent.jar" -D"skywalking.agent.service_name=micro-dev::micro-system" -D"skywalking.collector.backend_service=http://192.168.103.117:11800" -jar target/demo1-0.0.1-SNAPSHOT.jar



# source = "D:/Programs/MicroService/micro/demos/demo1-origin"
source = "D:/Programs/MicroService/traveldog/traveldog"
# source = "D:/Programs/MicroService/mall"
target = source + "-fuxii"

# 如果目标目录存在，则删除它
if os.path.exists(target):
    shutil.rmtree(target)

# 复制目录
shutil.copytree(source, target, dirs_exist_ok=True)

proj_name = source.split("/")[-1]
print(proj_name)
source = target

G = nx.DiGraph()

def static():
    global G
    print("hihi: ", source)
    
    original_dir = os.getcwd()
    
    # 切换到目标目录
    os.chdir(source)

    # 定义要执行的命令
    mvnw_command = [
        "mvnw.cmd",
        "dependency:copy-dependencies",
        "-DoutputDirectory=fuxi-static-dependency"
    ]

    # 执行 ./mvnw clean install 命令
    print("Executing ./mvnw dependency:copy-dependencies -DoutputDirectory=dependency...")
    try:
        mvnw_process = subprocess.run(mvnw_command, check=True)
        print("mvnw command completed successfully.")
    except Exception as e:
        print(f"Error executing mvnw command: {e}")
        sys.exit(1)  # 终止程序
        return
    
    os.chdir(original_dir)
    
    response = requests.post("http://127.0.0.1:18555/callGraph", data=source)
    # print(response.text)

    static_data = response.json()
    # print("static_data: ", static_data)

    # static_json = "D:/Programs/MicroService/call-graph/tools/callGraph.json"
    # with open(static_json, "r") as f:
    #     static_data = json.load(f)
    
    excluded_names = static_data.get("Excluded_Names", [])

    for caller, callees in static_data.items():
        if caller == "Excluded_Names": continue
        # print(f"Key: {caller}, Value: {callees}")
        G.add_node(caller, execution_time=0, count=0, type="function")
        # for caller, callee, attr in G.edges(data=True):
        #     print(caller, callee, attr)
        # # print(G.edges(data=True))
        print("caller: ", caller)
        print("callee: ", callees)
        for callee in callees:
            if callee not in static_data or callee in excluded_names or ".".join(callee.split("(")[0].split(".")[0:-1]) in excluded_names:
                continue
            G.add_node(callee, execution_time=0, count=0, type="function")
            G.add_edge(caller, callee, weight=1)
    # showGraph(G)

    generate_echarts_html(G, output_file="dag.html")


def dynamic():
    global G
    G = buildGraph(G, source='json', saveSpans=False, saveEntries=False)
    # showGraph(G)


def partition():
    global G
    # 修改工作目录到 main.py 所在的目录
    os.chdir("Project")

    partitions = stage1Main()

    os.chdir("..")
    
    return partitions

    # G = nx.read_graphml("Partition/data/target/microservice_graph.graphml")
    # showGraph(G)

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
    dest_folder = source + "-microo"
    
    # 如果目标目录存在，则删除它
    if os.path.exists(dest_folder):
        shutil.rmtree(dest_folder)

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



def get_jar_name(target_dir):
    """
    获取 target 目录下最新的 JAR 文件名称
    :param target_dir: Maven 构建的目标目录
    :return: JAR 文件的完整路径
    """
    # 构建目标目录路径
    target_path = os.path.join(target_dir + "/target", "*.jar")
    # 获取所有 JAR 文件
    jar_files = glob.glob(target_path)
    if not jar_files:
        raise FileNotFoundError(f"No JAR files found in {target_dir}")
    # 返回最新的 JAR 文件
    latest_jar = max(jar_files, key=os.path.getctime)
    return os.path.relpath(latest_jar, target_dir)


def run_project(port=None):
    original_dir = os.getcwd()
    
    # 切换到目标目录
    os.chdir(source)

    # 定义要执行的命令
    mvnw_command = ["mvnw.cmd", "clean", "install"]

    # 执行 ./mvnw clean install 命令
    print("Executing ./mvnw clean install...")
    try:
        mvnw_process = subprocess.run(mvnw_command, check=True)
        print("mvnw command completed successfully.")
    except Exception as e:
        print(f"Error executing mvnw command: {e}")
        sys.exit(1)  # 终止程序
        return

    # 执行 java 命令
    print("Starting Java application...")
    # java_process = subprocess.run(java_command)
    java_command = ["java", 
                    "-javaagent:D:/Programs/MicroService/apache-skywalking-apm-10.2.0/apache-skywalking-apm-bin/agent/skywalking-agent.jar", 
                    "-Dskywalking.agent.service_name=micro-dev::micro-system", 
                    "-Dskywalking.collector.backend_service=127.0.0.1:11800", 
                    "-jar", 
                    get_jar_name(source), 
                    f"--server.port={port}" if port else ""]
    try:
        java_process = subprocess.Popen(java_command, stdout=None, stderr=None)

        # 等待项目启动完毕
        print("Waiting for the Java application to start...")
        time.sleep(20)  # 假设项目启动需要10秒，你可以根据实际情况调整等待时间

        # 检查项目是否启动成功
        if java_process.poll() is None:
            print("Java application is running.")
            os.chdir(original_dir)
            
            input(f"waiting for test, port: {port}, click enter to continue...")
            # test(port)
        else:
            print("Java application failed to start.")
            print("Error:", java_process.stderr.read().decode())
    finally:
        java_process.terminate()  # 终止 Java 进程
    # 等待 Java 进程结束
    java_process.wait()
    print("Java application terminated.")
    os.chdir(original_dir)

def to_func_name(func):
    # print("func: ", func)
    funcName = func.split("(")[0]
    funcName = funcName.split(".")[-2] + "." + funcName.split(".")[-1]
    params = func.split("(")[-1].split(")")[0].split(",")
    params = [param.split(".")[-1] for param in params]
    return funcName + "(" + ", ".join(params) + ")"


def to_file_path(func):
    file_path = func.split("(")[0].split(".")[0:-1]
    file_path = "/".join(file_path)
    return file_path

def get_type(func):
    return G.nodes[func]["type"]

    
def save_to_json(partitions, G):
    
    with open("partitions.json", "w") as f:
        json.dump(partitions, f, indent=4)
    
    rawData = [{
        "func_name": to_func_name(func),
        "file_path": to_file_path(func),
        "full_name": func,
        "lineno": 0,
        "end_lineno": 0,
        "docstring": 0,
        "execution_time": 0,
        "count": 0,
        "label": label
    } for func, label in partitions.items() if get_type(func) == "function"]
    
    # print(G.edges(data=True))
    
    rawConnections = [{
        "caller_tuple": [
            to_file_path(caller),
            to_func_name(caller),
        ],
        "called_tuple": [
            to_file_path(callee),
            to_func_name(callee),
        ],
        "execution_time": 10,
        "call_count": attr["weight"] if "weight" in attr else 1,
    } for caller, callee, attr in G.edges(data=True) if get_type(caller) == "function" and get_type(callee) == "function"]
    
    print("rawConnections: ", rawConnections)
    
    vueData = {
        "rawData": rawData,
        "rawConnections": rawConnections,
    }
    
    with open("vueData.json", "w") as f:
        json.dump(vueData, f, indent=4)


def save_microservices(partitions, G):
    microservices: Dict[int, List[str]] = {}

    for node, communityID in partitions.items():
        if communityID not in microservices:
            microservices[communityID] = []
        microservices[communityID].append(node)
    
    output: Dict[int, Dict[str, List[str]]] = {}

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
        
    return output


def main():
    global G
    static()
    # showGraph(G)
    generate_echarts_html(G, output_file="dag.html")
    # # return
    # run_project()
    # print("run_project success!")
    # # return
    dynamic()
    
    generate_echarts_html(G, output_file="dag1.html")
    
    G.remove_nodes_from(list(nx.isolates(G)))
    # showGraph(G)
    generate_echarts_html(G, output_file="dag2.html")
    nx.write_graphml(G, "Project/data/src/graph.graphml")
    return
    
    ###########################################
    
    
    G = nx.read_graphml("Project/data/src/graph.graphml")
    
    partitions = partition()
    
    # with open("partitions.json", "r") as f:
    #     partitions = json.load(f)
    
    print("partitions: ", partitions)
    
    save_to_json(partitions, G)
    
    input(f"waiting for modify, click enter to continue...")
    
    with open("partitions_user.json", "r") as f:
        partitions = json.load(f)
        
    # # with open("microservices.json", "r") as f:
    # #     output = json.load(f)
    
    output = save_microservices(partitions, G)
    
    reconstruct(partitions, output)
    
    

# 获取当前脚本的绝对路径
current_dir = os.path.dirname(os.path.abspath(__file__))
# 获取 tools 模块所在的目录
tools_dir = os.path.join(current_dir, "Project")
# 将 tools 模块所在的目录添加到 sys.path
sys.path.insert(0, tools_dir)
from Project.Main import *

main()


