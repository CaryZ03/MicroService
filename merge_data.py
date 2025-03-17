import os
import json
from monkey_patch_trace import Config
from find_imports import CodeAnalyzer
from find_imports import FuncRelationShip
import shutil

double_select = {}


# 统计所有的信息的node，包括trace node和import node
class InformationNode:
    def __init__(self, importNode):
        self.importNode = importNode
        self.children = []
        self.father = None
        self.call_functions = []
        self.exec_time = 0.0
        self.exec_count = 0
        self.io_nodes = []
        self.db_nodes = []

    def to_dict(self):
        return {
            "importNode": self.importNode.to_dict() if self.importNode else None,
            "children": [child.to_dict() for child in self.children],
            "call_functions": [list(cf) for cf in self.call_functions],
            "exec_time": self.exec_time,
            "exec_count": self.exec_count,
            "io_nodes": [io_node.to_dict() for io_node in self.io_nodes],
            "db_nodes": [db_node.to_dict() for db_node in self.db_nodes],
            "father": list(self.father) if self.father else None
        }

    @staticmethod
    def from_dict(data):
        # 重建 FuncRelationShip 对象时需要传入 fileName，这里取自字典中的 fileName 字段
        import_node = FuncRelationShip.from_dict(data["importNode"], data["importNode"].get("fileName")) if data[
            "importNode"] else None
        node = InformationNode(import_node)
        node.exec_time = data["exec_time"]
        node.exec_count = data["exec_count"]
        node.io_nodes = [IONode.from_dict(io_node) for io_node in data["io_nodes"]]
        node.db_nodes = [DBNode.from_dict(db_node) for db_node in data["db_nodes"]]
        node.children = [InformationNode.from_dict(child) for child in data["children"]]
        node.call_functions = [tuple(cf) for cf in data["call_functions"]]
        node.father = tuple(data["father"]) if data["father"] else None
        return node

    def analyze_tree(self, node, nodeMap):
        global double_select
        if (node.importNode.func_name, node.importNode.fileName) not in double_select:
            double_select[(node.importNode.func_name, node.importNode.fileName)] = []
        double_select[(node.importNode.func_name, node.importNode.fileName)].append(
            (node.importNode.func_name, node.importNode.lineno, node.importNode.fileName, node.importNode.end_lineno))
        nodeMap[(
            node.importNode.func_name, node.importNode.lineno, node.importNode.fileName,
            node.importNode.end_lineno)] = node
        importNode = node.importNode
        for child in importNode.children:
            tmp_child = InformationNode(child)
            node.children.append(tmp_child)
            tmp_child.father = (importNode.func_name, importNode.lineno, importNode.fileName, importNode.end_lineno)
            self.analyze_tree(tmp_child, nodeMap)


class IONode:
    def __init__(self):
        self.io_function = None
        self.file = None
        self.line = None
        self.start_time = None
        self.call_stack = None
        self.exec_time = None

    def to_dict(self):
        return {
            'io_function': self.io_function,
            'file': self.file,
            'line': self.line,
            'start_time': self.start_time,
            'call_stack': self.call_stack,
            'exec_time': self.exec_time
        }

    @staticmethod
    def from_dict(data):
        self = IONode()
        self.io_function = data['io_function']
        self.file = data['file']
        self.line = data['line']
        self.start_time = data['start_time']
        self.call_stack = data['call_stack']
        self.exec_time = data['exec_time']
        return self


class DBNode:
    def __init__(self):
        self.db_function = None
        self.table = None
        self.sql = None
        self.start_time = None
        self.exec_time = None

    def to_dict(self):
        return {
            'db_function': self.db_function,
            'table': self.table,
            'sql': self.sql,
            'start_time': self.start_time,
            'exec_time': self.exec_time
        }

    @staticmethod
    def from_dict(data):
        self = DBNode()
        self.db_function = data['db_function']
        self.table = data['table']
        self.sql = data['sql']
        self.start_time = data['start_time']
        self.exec_time = data['exec_time']
        return self


class TraceNode:
    def __init__(self):
        self.called_function_name = None
        self.called_file = None
        self.called_lineno = None
        self.caller_function_name = None
        self.caller_file = None
        self.caller_lineno = None
        self.start_time = None
        self.exec_time = None
        self.caller_first_lineno = None
        self.io_nodes = []
        self.db_nodes = []

    def to_dict(self):
        return {
            'called_function_name': self.called_function_name,
            'called_file': self.called_file,
            'called_lineno': self.called_lineno,
            'caller_function_name': self.caller_function_name,
            'caller_file': self.caller_file,
            'caller_lineno': self.caller_lineno,
            'start_time': self.start_time,
            'exec_time': self.exec_time,
            'caller_first_lineno': self.caller_first_lineno,
            'db_nodes': [db_node.to_dict() for db_node in self.db_nodes],
            'io_nodes': [io_node.to_dict() for io_node in self.io_nodes]
        }

    def from_dict(self, data):
        self.called_function_name = data['called_function_name']
        self.called_file = data['called_file']
        self.called_lineno = data['called_lineno']
        self.caller_function_name = data['caller_function_name']
        self.caller_file = data['caller_file']
        self.caller_lineno = data['caller_lineno']
        self.start_time = data['start_time']
        self.exec_time = data['exec_time']
        self.caller_first_lineno = data['caller_first_lineno']
        self.io_nodes = [IONode.from_dict(io_node) for io_node in data['io_nodes']]
        self.db_nodes = [DBNode.from_dict(db_node) for db_node in data['db_nodes']]
        return self


def get_imports_nodes(json_dir):
    imports_result = []
    nodeMap = {}
    for file in os.listdir(json_dir):
        if file.endswith('.json'):
            json_path = os.path.join(json_dir, file)
            analyzer = CodeAnalyzer(file.replace('&', '/').replace('.json', '.py'))
            analyzer.load_from_json(json_path)
            root_node = InformationNode(analyzer.root_node)
            root_node.analyze_tree(root_node, nodeMap)
            imports_result.append((file.replace('&', '/').replace('.json','.py'), root_node))

    return imports_result, nodeMap


def save_merged_results(imports_results, file_path):
    data = []
    for file_name, root_node in imports_results:
        item = {
            "file": file_name,
            "root_node": root_node.to_dict()
        }
        data.append(item)
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=4)


def save_map_results(nodeMap, file_path):
    data = []
    for key, value in nodeMap.items():
        data.append({
            "key": key,
            "value": value.to_dict()
        })
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=4)


def load_imports_results(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)
    imports_results = []
    for item in data:
        file_name = item["file"]
        root_node = InformationNode.from_dict(item["root_node"])
        imports_results.append((file_name, root_node))
    return imports_results


def get_trace_data(config):
    json_dir = config.config['trace_output_dir']
    trace_result = []
    for file in os.listdir(json_dir):
        if file.endswith('.json'):
            with open(os.path.join(json_dir, file), 'r') as f:
                data = json.load(f)
            trace_data = data['trace_data']
            for key, value in trace_data.items():
                thread_list = {key: []}
                for item in value:
                    node = TraceNode()
                    node.from_dict(item)
                    thread_list[key].append(node)
                trace_result.append(thread_list)
    return trace_result


def merge_result(trace_data, nodeMap, root_dir):
    global double_select
    for thread in trace_data:
        for key, value in thread.items():
            for item in value:
                if item.called_file is not None and item.caller_file is not None and item.called_file.startswith(
                        root_dir):
                    called_path = item.called_file.removeprefix(root_dir).removeprefix('/')
                    caller_path = item.caller_file.removeprefix(root_dir).removeprefix('/')
                    called_lineno = item.called_lineno
                    called_func = item.called_function_name
                    caller_func = item.caller_function_name
                    caller_lineno = item.caller_lineno
                    called_tuple = None
                    caller_tuple = None

                    if called_func in ('open', 'read', 'write', 'close'):
                        io_node = IONode()
                        io_node.io_function = called_func
                        io_node.file = called_path
                        io_node.line = called_lineno
                        io_node.start_time = item.start_time
                        io_node.exec_time = item.exec_time
                        item.io_nodes.append(io_node)
                        if (caller_func, caller_path) in double_select:
                            for call_tuple in double_select[(caller_func, caller_path)]:
                                if call_tuple[1] <= caller_lineno <= call_tuple[3]:
                                    caller_tuple = call_tuple
                                    break
                            if caller_tuple is not None:
                                caller_node = nodeMap[caller_tuple]
                                caller_node.io_nodes.extend(item.io_nodes)

                    if (called_func, called_path) in double_select:
                        for call_tuple in double_select[(called_func, called_path)]:
                            if call_tuple[1] <= called_lineno <= call_tuple[3]:
                                called_tuple = call_tuple
                                break
                        if called_tuple is not None:
                            called_node = nodeMap[called_tuple]
                            called_node.db_nodes.extend(item.db_nodes)
                            called_node.io_nodes.extend(item.io_nodes)

                    if (called_func, called_path) not in double_select or (
                            caller_func, caller_path) not in double_select:
                        continue

                    for call_tuple in double_select[(called_func, called_path)]:
                        if call_tuple[1] <= called_lineno <= call_tuple[3]:
                            called_tuple = call_tuple
                            break
                    for call_tuple in double_select[(caller_func, caller_path)]:
                        if call_tuple[1] <= caller_lineno <= call_tuple[3]:
                            caller_tuple = call_tuple
                            break

                    if called_tuple is not None and caller_tuple is not None:
                        called_node = nodeMap[called_tuple]
                        caller_node = nodeMap[caller_tuple]
                        caller_node.call_functions.append(called_tuple)
                        called_node.exec_time += item.exec_time
                        called_node.exec_count += 1


def main():
    config_path = 'config/config.json'
    config = Config(config_path)
    root_dir = config.config['root_dir']
    imports_result, nodeMap = get_imports_nodes(config.config['imports_output_dir'])
    trace_data = get_trace_data(config)
    merge_result(trace_data, nodeMap, root_dir)
    map_output_dir_path = config.config['map_output_dir']
    shutil.rmtree(map_output_dir_path, ignore_errors=True)
    os.makedirs(map_output_dir_path, exist_ok=True)
    save_merged_results(imports_result, os.path.join(map_output_dir_path, 'merged_result.json'))
    save_map_results(nodeMap, os.path.join(map_output_dir_path, 'map_result.json'))

if __name__ == '__main__':
    main()
