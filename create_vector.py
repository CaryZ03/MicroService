"""
将项目源码的有效信息提取出来，包括：
1. 函数调用关系
2. 导入的库
3. 返回值
4. 类名
5. 数据库调用
"""
from merge_data import InformationNode
from find_imports import Config
import json
import os
from merge_data import main as merge_data_main


def from_dict(data):
    vector_node = VectorNode()
    vector_node.function_id = tuple(data['function_id'])
    vector_node.call_relations = [tuple(call_relation) for call_relation in data['call_relations']]
    vector_node.imported_libraries = data['imported_libraries']
    vector_node.return_names = data['return_names']
    vector_node.class_name = data
    vector_node.database_calls = data['database_calls']
    vector_node.children_count = data['children_count']
    vector_node.node_type = data['node_type']
    vector_node.total_calls = data['total_calls']
    return vector_node


class VectorNode:
    """
    这个是记录需要转换成向量的节点中的信息，包括：
    1. 函数调用关系
    2. 导入的库
    3. 返回值
    4. 类名
    5. 数据库调用
    6. 子方法数量
    7. 节点类型
    8. 总被调用次数
    """

    def __init__(self):
        self.function_id = None
        self.call_relations = []
        self.imported_libraries = []
        self.return_names = []
        self.class_name = []
        self.database_calls = []
        self.children_count = 0
        self.node_type = None
        self.total_calls = 0

    def __str__(self):
        return f'{self.function_id}'

    def to_dict(self):
        return {
            "function_id": list(self.function_id),
            "call_relations": [list(call_relation) for call_relation in self.call_relations],
            "imported_libraries": self.imported_libraries,
            "return_names": self.return_names,
            "class_name": self.class_name,
            "database_calls": self.database_calls,
            "children_count": self.children_count,
            "node_type": self.node_type,
            "total_calls": self.total_calls
        }


class TreeNode:
    """
    这里是针对类和函数的节点，只有顶层节点才能够拥有child，其余的下层节点的父节点只能是顶层节点
    """

    def __init__(self):
        self.function_id = None
        self.father = None
        self.children = []

    def add_child(self, child):
        self.children.append(child)

    def __str__(self):
        return f'{self.function_id},father:{self.father.function_id if self.father is not None else None}'


def judge_import_valid(import_node):
    return True


def create_vector(call_chain_result, nodeMap, class_map):
    dict_map = {}
    for key, value in call_chain_result.items():
        if key in class_map and key == class_map[key].father.function_id:
            vector_node = VectorNode()
            dict_map[key] = vector_node
            vector_node.function_id = key
            vector_node.class_name = []
            for return_variable in nodeMap[key].importNode.return_variables:
                dict_map[key].return_names.append(return_variable)
            vector_node.class_name.append(key[0])
            vector_node.node_type = nodeMap[key].importNode.node_type
            if vector_node.node_type == 'class':
                vector_node.children_count = len(nodeMap[key].children)
            else:
                vector_node.children_count = 1

    for key, value in call_chain_result.items():
        for call_relation in value:
            dict_map[class_map[key].father.function_id].call_relations.append(
                class_map[call_relation].father.function_id)
            dict_map[class_map[call_relation].father.function_id].total_calls += 1
        for db_node in nodeMap[key].db_nodes:
            dict_map[class_map[key].father.function_id].database_calls.append(db_node.table)
        for import_node in nodeMap[key].importNode.imports:
            if judge_import_valid(import_node):
                dict_map[class_map[key].father.function_id].imported_libraries.append(import_node[0])

    return list(dict_map.values())


def create_map_vector(file_path, merged_result, result):
    func_name = merged_result.importNode.func_name
    lineno = merged_result.importNode.lineno
    end_lineno = merged_result.importNode.end_lineno
    result[(func_name, lineno, file_path, end_lineno)] = []
    for call_function in merged_result.call_functions:
        result[(func_name, lineno, file_path, end_lineno)].append(call_function)
    for child in merged_result.children:
        create_map_vector(file_path, child, result)


def read_json(json_path):
    with open(json_path, 'r') as f:
        return json.load(f)


def analyze_json(merged_result):
    result = {}
    for key, value in merged_result.items():
        create_map_vector(key, value, result)

    return result


def analyze_nodeMap_json(nodeMap):
    result_node_map = {}
    for node in nodeMap:
        key = node['key']
        value = node['value']
        result_node_map[tuple(key)] = InformationNode.from_dict(value)
    return result_node_map


def build_tree_helper(node, tree_node_map, current_father=None):
    current_node = TreeNode()
    current_node.function_id = (
        node.importNode.func_name, node.importNode.lineno, node.importNode.fileName, node.importNode.end_lineno)
    tree_node_map[current_node.function_id] = current_node
    if current_father is None:
        current_father = current_node
    else:
        current_father.add_child(current_node)
    current_node.father = current_father
    for child in node.children:
        build_tree_helper(child, tree_node_map, current_father)


def analyze_merged_result_json(merged_result):
    result_merged_result = {}
    tree_node_map = {}
    for node in merged_result:
        file_path = node['file']
        root_node = InformationNode.from_dict(node['root_node'])
        result_merged_result[file_path] = root_node
        for child in root_node.children:
            build_tree_helper(child, tree_node_map)
    return result_merged_result, tree_node_map


def save_to_json(data, json_path):
    dict = []
    for item in data:
        dict.append({
            "key": list(item.function_id),
            "value": item.to_dict()
        })
    with open(json_path, 'w') as f:
        json.dump(dict, f)


def main():
    merge_data_main()
    config_path = 'config/config.json'
    config = Config(config_path)
    merged_json_path = os.path.join(config.config['map_output_dir'], 'merged_result.json')
    map_json_path = os.path.join(config.config['map_output_dir'], 'map_result.json')
    merged_result_json = read_json(merged_json_path)
    nodeMap = analyze_nodeMap_json(read_json(map_json_path))
    merged_result, class_map = analyze_merged_result_json(merged_result_json)
    call_chain_result = analyze_json(merged_result)
    functions = create_vector(call_chain_result, nodeMap, class_map)
    os.makedirs(config.config['vector_output_dir'], exist_ok=True)
    save_to_json(functions, os.path.join(config.config['vector_output_dir'], 'vector.json'))


if __name__ == '__main__':
    main()
