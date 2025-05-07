import json
from typing import Any, Dict, List, Tuple

from parse_config import Config


def load_json_data(json_path: str) -> Any:
    """
    从 JSON 文件中加载数据。
    :param json_path: JSON 文件路径。
    :return: 获取到的数据
    """
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    return data


class InputDataGenerator:
    """
    InputDataGenerator 类，用于生成输入数据。
    """

    def __init__(self, config_path: str):
        self.config: Dict[str, str] = Config(config_path).config
        self.call_chains: Dict[str, List[Any]] = load_json_data(self.config['call_chains_json_path'])
        self.all_services: List[Any] = []
        self.tackled_chains: Dict[str, List[Any]] = {}
        self.graph_pairs: Dict[Tuple[str, str], float] = {}

    @staticmethod
    def get_num(input_str: str) -> float:
        num = ''
        for i in range(len(input_str)):
            if input_str[i] not in ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.']:
                return float(input_str[:i])
            else:
                num += input_str[i]
        return float(num)

    def generate_call_chains(self):
        for trace_id, call_chain in self.call_chains.items():
            for entry in call_chain:
                endpointName = entry['endpointName']
                splits = endpointName.split('.')
                if 'B' not in splits[-1]:
                    continue
                data_transmitted = splits[-1]
                service_name = '.'.join(splits[:-1])
                self.all_services.append(service_name)
                if trace_id not in self.tackled_chains:
                    self.tackled_chains[trace_id] = []
                self.tackled_chains[trace_id].append({
                    'serviceName': service_name,
                    'dataTransmitted': data_transmitted,
                    'spanId': entry['spanId'],
                    'parentId': entry['parentSpanId'],
                })
        self.all_services = list(set(self.all_services))

    def generate_call_graph(self):
        for trace_id, call_chain in self.tackled_chains.items():
            tmp_dict: Dict[int, Any] = {}
            for chain_node in call_chain:
                tmp_dict[chain_node['spanId']] = chain_node
            for chain_node in call_chain:
                if chain_node['parentId'] in tmp_dict:
                    if (tmp_dict[chain_node['parentId']]['serviceName'], chain_node['serviceName']) in self.graph_pairs:
                        self.graph_pairs[
                            (tmp_dict[chain_node['parentId']]['serviceName'], chain_node['serviceName'])] += \
                            self.get_num(chain_node['dataTransmitted'])
                    else:
                        self.graph_pairs[
                            (tmp_dict[chain_node['parentId']]['serviceName'], chain_node['serviceName'])] = \
                            self.get_num(chain_node['dataTransmitted'])

    def save_graph_pairs(self):
        service_len = len(self.all_services)
        edge_len = len(self.graph_pairs)
        edges = []
        for key, value in self.graph_pairs.items():
            edges.append({
                'source': key[0],
                'target': key[1],
                'dataTransmitted': value
            })
        with open(self.config['graph_pairs_json_path'], 'w', encoding='utf-8') as f:
            json.dump({
                'service_len': service_len,
                'edge_len': edge_len,
                'all_services': self.all_services,
                'graph_pairs': edges
            }, f, ensure_ascii=False, indent=4)

    def generate_input_data(self):
        self.generate_call_chains()
        self.generate_call_graph()
        self.save_graph_pairs()
        for key, value in self.graph_pairs.items():
            print(f"Service Pair: {key[0]} -> {key[1]}, Data Transmitted: {value}")


if __name__ == '__main__':
    input_data_generator = InputDataGenerator('config.json')
    input_data_generator.generate_input_data()
