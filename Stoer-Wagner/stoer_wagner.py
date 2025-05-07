import json
import sys
from copy import deepcopy
from typing import Any, Dict, List, Tuple

from parse_config import Config


def create_partition_node(nodes: List[str], cut_value: int = None):
    return {
        "nodes": nodes,
        "cut_value": cut_value,
        "children": []
    }


class StoerWagner:
    def __init__(self, config_path):
        self.tmp_map = {}
        self.config = Config(config_path).config
        self.json_data = self.load_json_data(self.config['new_graph_pairs_json_path'])
        self.id_node_map: Dict[int, str] = {}
        self.node_id_map: Dict[str, int] = {}
        self.all_services: List[str] = self.json_data['all_services']
        self.partitions = []  # 保存所有划分结果
        self.partition_tree = None  # 划分树结构

    @staticmethod
    def load_json_data(json_path: str) -> Any:
        with open(json_path, 'r', encoding='utf-8') as f:
            return json.load(f)

    def find(self, father, x):
        if father[x] == x:
            return x
        father[x] = self.find(father, father[x])
        return father[x]

    def add(self, father, a, b):
        fa = self.find(father, a)
        fb = self.find(father, b)
        if fa < fb:
            father[fb] = fa
        else:
            father[fa] = fb

    @staticmethod
    def stoer_wagner(n, g):
        new_graph = deepcopy(g)
        vis1 = [False] * (n + 1)
        ans = sys.maxsize
        vertices = [{i} for i in range(n + 1)]  # 第 i 个点代表的集合（初始为自己）

        best_partition = (set(), set())

        for i in range(1, n):
            w = [0] * (n + 1)
            vis2 = [False] * (n + 1)
            s = t = 0

            for j in range(1, n - i + 2):
                now = 0
                for k in range(1, n + 1):
                    if not vis1[k] and not vis2[k] and w[k] >= w[now]:
                        now = k
                s, t = t, now
                vis2[now] = True
                for k in range(1, n + 1):
                    w[k] += new_graph[now][k]

            if w[t] < ans:
                ans = w[t]
                best_partition = (deepcopy(vertices[t]), set())
                for idx in range(1, n + 1):
                    if not vis1[idx] and idx != t:
                        best_partition[1].update(vertices[idx])

            vis1[t] = True
            if s != t:
                vertices[s].update(vertices[t])
                for j in range(1, n + 1):
                    if j != s:
                        new_graph[s][j] += new_graph[t][j]
                        new_graph[j][s] += new_graph[j][t]

        return ans, {0: list(best_partition[0]), 1: list(best_partition[1])}

    def build_graph(self, input_data):
        n = input_data["service_len"]
        g = [[0] * (n + 1) for _ in range(n + 1)]
        for i in range(len(self.all_services)):
            self.node_id_map[self.all_services[i]] = i + 1
            self.id_node_map[i + 1] = self.all_services[i]

        for edge in input_data["graph_pairs"]:
            u = self.node_id_map[edge["source"]]
            v = self.node_id_map[edge["target"]]
            w = int(edge["dataTransmitted"])
            g[u][v] += w
            g[v][u] += w  # 无向图
        return n, g

    @staticmethod
    def extract_subgraph(g, nodes: List[int]) -> Tuple[int, List[List[int]], Dict[int, int]]:
        idx_map = {old: new_idx + 1 for new_idx, old in enumerate(nodes)}
        new_n = len(nodes)
        new_g = [[0] * (new_n + 1) for _ in range(new_n + 1)]

        for i in range(len(nodes)):
            for j in range(len(nodes)):
                u = idx_map[nodes[i]]
                v = idx_map[nodes[j]]
                new_g[u][v] = g[nodes[i]][nodes[j]]

        new_idx_map = {new_idx: old for old, new_idx in idx_map.items()}
        return new_n, new_g, new_idx_map

    def recursive_partition(self, n, g, full_g, r: int, id_map: Dict[int, int], current_node: Dict = None):
        if r <= 0 or n <= 2:
            return

        cut_value, clusters = self.stoer_wagner(n, g)
        new_clusters = {}
        for cluster_id, nodes in clusters.items():
            new_nodes = [self.id_node_map[id_map[node]] for node in nodes]
            new_clusters[cluster_id] = new_nodes
        self.partitions.append((cut_value, new_clusters))

        if current_node is not None:
            current_node["cut_value"] = cut_value

        for cluster_nodes in clusters.values():
            if len(cluster_nodes) <= 1:
                continue

            new_cluster_nodes = [id_map[node] for node in cluster_nodes]
            sub_n, sub_g, idx_map = self.extract_subgraph(full_g, new_cluster_nodes)

            node_names = [self.id_node_map[id_map[node]] for node in cluster_nodes]
            child_node = create_partition_node(node_names)
            if current_node is not None:
                current_node["children"].append(child_node)

            self.recursive_partition(sub_n, sub_g, full_g, r - 1, idx_map, child_node)

    def run(self, r: int):
        n, graph = self.build_graph(self.json_data)
        id_map = {i: i for i in range(1, n + 1)}
        all_nodes = [self.id_node_map[i] for i in range(1, n + 1)]
        self.partition_tree = create_partition_node(all_nodes)
        self.recursive_partition(n, graph, graph, r, id_map, self.partition_tree)

        # 输出划分结果树
        with open('partition_tree.json', 'w', encoding='utf-8') as f:
            json.dump(self.partition_tree, f, ensure_ascii=False, indent=2)

        # 控制台打印每一次划分
        for partition in self.partitions:
            print(partition)


if __name__ == "__main__":
    config_path = './config.json'
    r_times = 20
    sw = StoerWagner(config_path)
    sw.run(r_times)
