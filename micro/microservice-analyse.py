from mapper import buildGraph, showGraph
import networkx as nx
import time
import requests
import shutil
import subprocess

import os
import sys
import json

from graph import generate_echarts_html
from test import test

source_main = "D:/Projects/MicroService/traveldog/microservice"

proj_name = source_main.split("/")[-1]
print(proj_name)

def generate(source):
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

def static(source):
    
    response = requests.post("http://127.0.0.1:18555/staticNode", data=source)
    # print(response.text)

    static_data = response.json()
    print("static_data: ", static_data)
    
    return static_data




def main():
    names = ["User_Service", "Order_Service", "Train_Service", "Hotel_Service", "Location_Service"]
    generate(source_main)
    partitions = {}
    for name in names:
        partitions[name] = static(source_main + "/" + name)

    print("partitions: ", partitions)
    
    with open("microservice-partitions.json", "w") as f:
        json.dump(partitions, f, indent=4)

main()
    