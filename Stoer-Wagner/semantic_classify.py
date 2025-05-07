import json
from typing import List

import numpy as np
from sklearn.cluster import AgglomerativeClustering
from sklearn.metrics import silhouette_score

from parse_config import Config


class SemanticClassifier:
    def __init__(self, config_path):
        self.config = Config(config_path).config
        self.semantic_matrix: np.ndarray = np.load(self.config['semantic_similarity_numpy_path'])
        for i in range(self.semantic_matrix.shape[0]):
            for j in range(self.semantic_matrix.shape[1]):
                if self.semantic_matrix[i][j] < 0.8:
                    self.semantic_matrix[i][j] = 0
        self.json_data = self.load_json_data(self.config['graph_pairs_json_path'])
        self.all_service: List[str] = self.json_data['all_services']

    def load_json_data(self, json_path):
        with open(json_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        return data

    @staticmethod
    def hierarchical_clustering(similarity_matrix, n_clusters=2):
        """
        使用层次聚类对相似性矩阵进行聚类
        :param similarity_matrix: 相似性矩阵
        :param n_clusters: 聚类数
        :return: 聚类标签
        """
        clustering = AgglomerativeClustering(n_clusters=n_clusters, metric='precomputed', linkage='average')
        labels = clustering.fit_predict(1 - similarity_matrix)  # 将相似性转换为距离
        return labels

    def find_best_n_clusters(self, dependence_matrix):
        """
        通过轮廓系数来确定最佳的聚类数
        :param dependence_matrix: 语义相关度矩阵
        :return: 最佳聚类数
        """
        max_score = -1
        best_n_clusters = 2
        for n_clusters in range(2, 11):
            try:
                labels = self.hierarchical_clustering(dependence_matrix, n_clusters)
                unique_labels = np.unique(labels)
                if len(unique_labels) < 2:
                    print(f"Skipping n_clusters = {n_clusters} because all points are in one cluster.")
                    continue
                distance_matrix = 1 - dependence_matrix
                np.fill_diagonal(distance_matrix, 0)
                score = silhouette_score(distance_matrix, labels, metric='precomputed')
                if score > max_score:
                    max_score = score
                    best_n_clusters = n_clusters
            except ValueError as e:
                print(f"Error occurred for n_clusters = {n_clusters}: {e}")
                continue
        return best_n_clusters

    def save_new_graph_pairs(self, labels):
        graph_pairs = self.json_data['graph_pairs']
        node_id_map = {service_name: i for i, service_name in enumerate(self.all_service)}
        new_graph_pairs_map = {}
        for graph_pair in graph_pairs:
            source = graph_pair['source']
            target = graph_pair['target']
            weight = graph_pair['dataTransmitted']
            if int(labels[node_id_map[source]]) == int(labels[node_id_map[target]]):
                continue
            if (int(labels[node_id_map[source]]), int(labels[node_id_map[target]])) not in new_graph_pairs_map:
                new_graph_pairs_map[(int(labels[node_id_map[source]]), int(labels[node_id_map[target]]))] = 0
            new_graph_pairs_map[(int(labels[node_id_map[source]]), int(labels[node_id_map[target]]))] += weight

        service_len = int(max(labels)) + 1
        graph_pairs_len = len(new_graph_pairs_map)
        new_graph_pairs = [{'source': i[0], 'target': i[1], 'dataTransmitted': j} for i, j in new_graph_pairs_map.items()]
        all_services = [int(i) for i in range(service_len)]
        new_graph_pairs_result = {
            'service_len': service_len,
            'edge_len': graph_pairs_len,
            'all_services': all_services,
            'graph_pairs': new_graph_pairs
        }
        with open(self.config['new_graph_pairs_json_path'], 'w', encoding='utf-8') as f:
            json.dump(new_graph_pairs_result, f, indent=4)

    def classify(self):
        """
        对函数进行分类
        :return: None
        """
        best_n_clusters = self.find_best_n_clusters(self.semantic_matrix)
        print(f"Best number of clusters: {best_n_clusters}")
        labels = self.hierarchical_clustering(self.semantic_matrix, best_n_clusters)
        self.save_new_graph_pairs(labels)
        label_match = {}
        for i, label in enumerate(labels):
            label = int(label)
            if label not in label_match:
                label_match[label] = []
            label_match[label].append(self.all_service[i])
        with open(self.config['label_match_json_path'], 'w', encoding='utf-8') as f:
            json.dump(label_match, f, indent=4)


def main():
    config_path = 'config.json'
    classifier = SemanticClassifier(config_path)
    classifier.classify()


if __name__ == '__main__':
    main()
