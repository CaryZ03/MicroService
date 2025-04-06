from mapper import buildGraph, showGraph
import networkx as nx
import time

import os
import sys

G = buildGraph(source='json', saveSpans=False, saveEntries=False)
showGraph(G)

nx.write_graphml(G, "Partition/data/src/graph.graphml")

# 获取当前脚本的绝对路径
current_dir = os.path.dirname(os.path.abspath(__file__))

# 获取 tools 模块所在的目录
tools_dir = os.path.join(current_dir, "Partition")

# 将 tools 模块所在的目录添加到 sys.path
sys.path.insert(0, tools_dir)

# 修改工作目录到 main.py 所在的目录
os.chdir("Partition")

from Partition.Main import *

stage2Main()

os.chdir("..")

G = nx.read_graphml("Partition/data/target/microservice_graph.graphml")
showGraph(G)

