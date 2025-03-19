"""
衡量不同类之间的相关度
两个衡量指标：
1. 方法依赖度：被调用的方法次数占总方法个数的百分比
2. 语义相似度：使用spacy doc计算两个类或方法的语义相似度，这里认为名词才是真正携带了信息，并且需要忽视掉所有的词形变化
"""
import json
import os
import numpy as np
import create_vector
from config_class import Config
import spacy
import re
import shutil

nlp = spacy.load("en_core_web_md")


def get_func_dependence(func1: create_vector.VectorNode, func2: create_vector.VectorNode) -> float:
    """
    计算两个函数之间的方法依赖度
    :param func1: 函数1
    :param func2: 函数2
    :return: 依赖度
    """
    call_relations1 = func1.call_relations
    call_relations2 = func2.call_relations
    count1 = 0
    for call_relation in call_relations1:
        if tuple(call_relation) == tuple(func2.function_id):
            count1 += 1
    count2 = 0
    for call_relation in call_relations2:
        if tuple(call_relation) == tuple(func1.function_id):
            count2 += 1
    dependency1 = count1 / func2.total_calls if func2.total_calls != 0 else 0
    dependency2 = count2 / func2.total_calls if func2.total_calls != 0 else 0
    alpha = 0.5
    beta = 0.5
    return alpha * dependency1 + beta * dependency2


def extract_nouns(text):
    """
    识别出句子中所有的名词
    :param text: 输入的文本
    :return: 所有的名词
    """
    # 加载英语模型

    doc = nlp(text)

    # 提取句子中的所有名词以及宾语
    nouns = []

    if len(doc) == 1:
        return text  # 假设单个词直接作为名词返回

    # 遍历词汇并提取名词
    for token in doc:
        # 如果是名词，或者是宾语，也作为名词处理
        if token.pos_ == "NOUN" or token.pos_ == "PROPN" or token.dep_ in ("dobj", "iobj"):
            nouns.append(token.text)

    return " ".join(nouns)


def calculate_similarity(phrase1, phrase2):
    # 使用词形还原处理短语
    nlp = spacy.load("en_core_web_md")
    doc1 = nlp(phrase1)
    doc2 = nlp(phrase2)

    # 还原词形
    lemma1 = " ".join([token.lemma_ for token in doc1])
    lemma2 = " ".join([token.lemma_ for token in doc2])

    # 将还原后的短语转化为spaCy文档对象并计算相似度
    doc1_lemma = nlp(lemma1)
    doc2_lemma = nlp(lemma2)

    similarity = doc1_lemma.similarity(doc2_lemma)
    return similarity


def get_dependence(func_list, config):
    """
    计算函数之间的依赖度
    :param func_list: 函数列表
    :param config: 配置文件
    :return: 无
    """
    func_count = len(func_list)
    call_chain_numpy = np.zeros((func_count, func_count))
    semantic_similarity_numpy = np.zeros((func_count, func_count))
    for i in range(func_count):
        for j in range(i + 1, func_count):
            print(f'Calculating {i} -> {j}')
            func1 = func_list[i]
            func2 = func_list[j]
            call_chain_numpy[i][j] = get_func_dependence(func1, func2)
            call_chain_numpy[j][i] = call_chain_numpy[i][j]
            semantic_similarity_numpy[i][j] = get_semantic_similarity(func1, func2,
                                                                      config['semantic_similarity_weights'])
            semantic_similarity_numpy[j][i] = semantic_similarity_numpy[i][j]

    for i in range(len(func_list)):
        for j in range(i + 1, len(func_list)):
            print(f'{func_list[i].function_id[0]} -> {func_list[j].function_id[0]}: {semantic_similarity_numpy[i][j]}')

    save_matrix_numpy(semantic_similarity_numpy,
                      os.path.join(config['dependence_output_dir'], 'semantic_similarity_numpy.npy'))
    save_matrix_numpy(call_chain_numpy,
                      os.path.join(config['dependence_output_dir'], 'call_chain_numpy.npy'))


def split_function_name(func_name):
    """
    拆分函数名（支持下划线命名和驼峰命名）
    """
    # 处理下划线命名
    if '_' in func_name:
        words = func_name.split('_')
    else:
        # 处理驼峰命名
        words = [word.lower() for word in
                 re.sub('([A-Z][a-z]+)', r' \1', re.sub('([A-Z]+)', r' \1', func_name)).split()]
    return ' '.join(words)


def min_max_normalize(matrix):
    """
    Min-Max 归一化
    :param matrix: 输入矩阵
    :return: 归一化后的矩阵
    """
    min_val = np.min(matrix)
    max_val = np.max(matrix)
    if max_val == min_val:
        return np.zeros_like(matrix)  # 避免除以零
    return (matrix - min_val) / (max_val - min_val)


def get_semantic_similarity(func1, func2, weights=None) -> float:
    """
    计算两个类之间的语义相似度
    :param func1: 类1
    :param func2: 类2
    :param weights: 权重列表 [文件名+方法名权重, 数据库权重, 自定义库权重]
    :return: 语义相似度（范围 [0, 1]）
    """
    if weights is None:
        weights = {
            'name_similarity': 0.55,
            'database_similarity': 0.35,
            'import_similarity': 0.1
        }

    # 提取文件名和方法名
    func_name1 = split_function_name(func1.function_id[0])
    func_name2 = split_function_name(func2.function_id[0])
    file_name1 = split_function_name(os.path.basename(func1.function_id[2]).removesuffix(".py"))
    file_name2 = split_function_name(os.path.basename(func2.function_id[2]).removesuffix(".py"))

    # 1. 提取名词并计算文件名 + 方法名的语义相似度
    text1 = f"{func_name1}"
    text2 = f"{func_name2}"
    nouns1 = extract_nouns(text1)  # 提取名词
    nouns2 = extract_nouns(text2)  # 提取名词
    semantic_sim = calculate_similarity(nouns1, nouns2)

    # 2. 计算数据库调用的相似度
    database1 = set(func1.database_calls)
    database2 = set(func2.database_calls)
    database_sim = len(database1.intersection(database2)) / max(len(database1), len(database2), 1)
    if len(database1) == 0 or len(database2) == 0:
        database_sim = semantic_sim

    # 3. 计算自定义库调用的相似度
    imported_libraries1 = set(func1.imported_libraries)
    imported_libraries2 = set(func2.imported_libraries)
    library_sim = len(imported_libraries1.intersection(imported_libraries2)) / max(len(imported_libraries1),
                                                                                   len(imported_libraries2), 1)

    # 4. 综合相似度
    total_sim = (
            weights['name_similarity'] * semantic_sim +
            weights['database_similarity'] * database_sim +
            weights['import_similarity'] * library_sim
    )
    return total_sim


def restore_from_map(json_path):
    """
    从json文件中恢复函数和类
    :param json_path: json文件路径
    :return: 函数或类的一个字典，字典的键是四元组，值是一个VectorNode
    """
    with open(json_path, 'r') as f:
        map_vector = json.load(f)
    result = {}
    for item in map_vector:
        key = tuple(item['key'])
        value = item['value']
        result[key] = create_vector.from_dict(value)
    return result


def save_matrix_numpy(matrix, path='./dependence_matrix.npy'):
    """
    保存矩阵为 Numpy 文件
    :param matrix: 输入矩阵
    :param path: 保存路径
    """
    np.save(path, matrix)


def save_func_list(func_list, path='./func_list.json'):
    """
    保存函数列表为 JSON 文件
    :param func_list: 函数列表
    :param path: 保存路径
    """
    with open(path, 'w') as f:
        json.dump([list(func.function_id) for func in func_list], f, indent=4)


def main():
    create_vector.main()
    config_path = 'config/config.json'
    config = Config(config_path)
    shutil.rmtree(config.config['dependence_output_dir'], ignore_errors=True)
    os.makedirs(config.config['dependence_output_dir'], exist_ok=True)
    func_map = restore_from_map(os.path.join(config.config['vector_output_dir'], 'vector.json'))
    func_list = list(func_map.values())
    print([key[0] for key in func_map.keys()])
    get_dependence(func_list, config.config)
    save_func_list(func_list, os.path.join(config.config['dependence_output_dir'], 'func_list.json'))


if __name__ == "__main__":
    main()
