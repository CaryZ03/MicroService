"""
利用聚类算法对节点进行聚类，具体方法：
1. 首先利用轮廓系数来确定最佳的聚类数
2. 通过函数之间的语义相似度矩阵来划分出函数的聚类
3. 然后根据函数的依赖性关系进行聚类的优化
"""
import numpy as np
import json
from config_class import Config
from sklearn.cluster import AgglomerativeClustering
from sklearn.metrics import silhouette_score
import os


def find_best_n_clusters(dependence_matrix):
    """
    通过轮廓系数来确定最佳的聚类数
    :param dependence_matrix: 语义相关度矩阵
    :return: 最佳聚类数
    """
    max_score = -1
    best_n_clusters = 2
    for n_clusters in range(2, 11):
        try:
            labels = hierarchical_clustering(dependence_matrix, n_clusters)
            unique_labels = np.unique(labels)
            if len(unique_labels) < 2:
                print(f"Skipping n_clusters = {n_clusters} because all points are in one cluster.")
                continue
            distance_matrix = 1 - dependence_matrix
            np.fill_diagonal(distance_matrix, 0)  # 将对角线元素设置为 0
            score = silhouette_score(distance_matrix, labels, metric='precomputed')
            if score > max_score:
                max_score = score
                best_n_clusters = n_clusters
        except ValueError as e:
            print(f"Error occurred for n_clusters = {n_clusters}: {e}")
            continue
    return best_n_clusters


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


def load_func_list(file_path):
    """
    从文件中加载函数列表
    :param file_path: 文件路径
    :return: 函数列表
    """
    with open(file_path, 'r') as f:
        return json.load(f)


def optimize_cluster(call_chain_matrix, labels, func_list, threshold=0.5):
    """
    根据函数的依赖性关系进行聚类的优化
    当前的思路：
    考虑到有些语义完全和其它的内容无关的部分，划分方法如下：
        1. 将聚类中有且只有它一个节点的这种节点称作孤立节点，针对孤立节点进行处理
        2. 首先将每个聚类调用这种孤立节点的call_chain_numpy对应的点的权重加和
        3. 取最大的作为这个孤立节点的归属聚类
        4. 如果没有任何的聚类调用过它，则将其划分到一个聚类中，这种聚类负责存放孤立节点
    同时，如果某些聚类中的节点被其它聚类节点调用的时候，判断条件如下：
        1. 如果某个聚类调用这个节点的次数占比超过阈值，则将这个节点划分到这个聚类中
        2. 其它情况仍保持语义优先，即将这个节点划分到语义相似度最高的聚类中
    :param call_chain_matrix: 函数调用关系矩阵
    :param labels: 聚类标签
    :param func_list: 函数列表
    :param threshold: 将聚类忽略语义关系划分的阈值
    :return: 优化后的聚类标签
    """
    n_clusters = labels.max() + 1

    # 处理孤立节点情况
    for i in range(n_clusters):
        cluster_funcs = [j for j in range(len(labels)) if labels[j] == i]
        call_chain_count = {}
        if len(cluster_funcs) == 1:
            for j in range(len(func_list)):
                if i == j:
                    continue
                else:
                    if labels[j] not in call_chain_count:
                        call_chain_count[labels[j]] = 0
                    call_chain_count[labels[j]] += call_chain_matrix[j][cluster_funcs[0]]
            max_count = 0
            max_label = n_clusters
            for label, count in call_chain_count.items():
                if count > max_count:
                    max_count = count
                    max_label = label
            labels[cluster_funcs[0]] = max_label

    # 处理被其它聚类调用的情况
    for i in range(len(func_list)):
        call_chain_count = {}
        for j in range(len(func_list)):
            if i == j:
                continue
            else:
                if labels[j] not in call_chain_count:
                    call_chain_count[labels[j]] = 0
                call_chain_count[labels[j]] += call_chain_matrix[j][i]
        max_count = 0
        max_label = -1
        for label, count in call_chain_count.items():
            if count > max_count:
                max_count = count
                max_label = label
        if max_count > threshold:
            labels[i] = max_label

    return labels


def main():
    config_path = 'config/config.json'
    config = Config(config_path)

    func_list = load_func_list(os.path.join(config.config['dependence_output_dir'], 'func_list.json'))
    semantic_similarity_matrix = np.load(
        os.path.join(config.config['dependence_output_dir'], 'semantic_similarity_numpy.npy'))
    call_chain_matrix = np.load(os.path.join(config.config['dependence_output_dir'], 'call_chain_numpy.npy'))

    best_n_clusters = find_best_n_clusters(semantic_similarity_matrix)
    print(f"Best number of clusters: {best_n_clusters}")

    labels = hierarchical_clustering(semantic_similarity_matrix, n_clusters=best_n_clusters)
    labels = optimize_cluster(call_chain_matrix, labels, func_list)
    label_groups = {}
    for label, func in zip(labels, func_list):
        if label not in label_groups:
            label_groups[label] = []
        label_groups[label].append(func[0])
    # 打印按标签分组的函数
    for label, functions in label_groups.items():
        print(f"Label: {label}")
        for function in functions:
            print(f"  Function: {function}")


if __name__ == "__main__":
    main()
