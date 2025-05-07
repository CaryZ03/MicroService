import requests
import json
import re
import os
import shutil
from datetime import datetime, timedelta
from sqlglot import parse, exp
from typing import List, Dict, Any, Optional

from parse_config import Config


def load_pre_input_entries(entries_json_path: str) -> List[Dict[str, Any]]:
    """
    从 json 文件中加载预输入的内容。

    参数:
    entries_json_path (str): 包含入口列表的 JSON 路径。

    返回:
    list: 服务列表。
    """
    with open(entries_json_path, 'r', encoding='utf-8') as f:
        entries: List[Dict[str, Any]] = json.load(f)
    return entries


class GraphNode:
    """
    GraphNode 类，用于表示图中的节点。
    """

    def __init__(self, name: str = '', node_type: str = '', execution_time: float = 0.0, count: int = 0.0) -> None:
        self.node_id: str = name
        self.node_type: str = node_type
        self.execution_time: float = execution_time
        self.count: int = count
        self.children: set[str] = set()
        self.father: set[str] = set()

    def to_dict(self):
        return {
            "node_id": self.node_id,
            "node_type": self.node_type,
            "execution_time": self.execution_time,
            "count": self.count,
            "children": list(self.children),
            "father": list(self.father)
        }


def graph_node_from_dict(data: Dict[str, Any]) -> GraphNode:
    """
    从字典中加载数据
    :param data: 数据字典
    :return:
    """
    graph_node = GraphNode()
    graph_node.node_id = data["node_id"]
    graph_node.node_type = data["node_type"]
    graph_node.execution_time = data["execution_time"]
    graph_node.count = data["count"]
    graph_node.children = set(data["children"])
    graph_node.father = set(data["father"])
    return graph_node


class SkywalkingTracer:
    """
    SkywalkingTracer 类，用于查询 Skywalking 服务的追踪数据。
    """

    def __init__(self, config_path: str) -> None:
        self.config: Config = Config(config_path)
        self.has_pre_input_entries: bool = self.config.has_pre_input_entries
        if self.has_pre_input_entries:
            self.pre_input_entries: List[Dict[str, Any]] = load_pre_input_entries(
                self.config.config['entries_json_path'])
        self.services: List[Dict[str, Any]] = []
        self.final_chains: Dict[str, List[Dict[str, Any]]] = {}
        self.graph_node_map: Dict[str, GraphNode] = {}
        self.traced_services: List[str] = []

    def visit_graphql(self, query: str, variables: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        """
        访问 GraphQL 服务端点。
        :param query: 请求
        :param variables: 变量
        :return: 返回的数据
        """
        url: str = self.config.config['skywalking_backend_url'] + '/graphql'
        headers: Dict[str, str] = {
            "Content-Type": "application/json",
        }

        # 发送 POST 请求
        response: requests.Response = requests.post(url, json={"query": query, "variables": variables}, headers=headers)

        # 检查响应
        if response.status_code == 200:
            data: Dict[str, Any] = response.json()
        else:
            print('error: visit graphql failed')
            return None

        return data

    def get_services(self) -> List[Dict[str, Any]]:
        query: str = """
            query queryServices($layer: String!) {
                services: listServices(layer: $layer) {
                    id
                    value: name
                    label: name
                    group
                    layers
                    normal
                    shortName
                }
            }
            """

        variables: Dict[str, str] = {
            "layer": "GENERAL"
        }

        data: Optional[Dict[str, Any]] = self.visit_graphql(query, variables)

        if data:
            services: List[Dict[str, Any]] = data.get("data").get("services")
            return services
        return []

    def query_traces(self, service_id: str) -> List[Dict[str, Any]]:
        """
        通过服务 ID 查询追踪。
        :param service_id: 服务 ID
        :return: 追踪列表
        """
        query: str = """
            query queryTraces($condition: TraceQueryCondition) {
                data: queryBasicTraces(condition: $condition) {
                    traces {
                    key: segmentId
                    endpointNames
                    duration
                    start
                    isError
                    traceIds
                    }
                }
            }
            """

        time_gap: timedelta = timedelta(minutes=self.config.config['time_gap'])
        current_time: datetime = datetime.now()
        former_time: datetime = current_time - time_gap

        variables: Dict[str, Any] = {
            "condition": {
                "queryDuration": {
                    "start": former_time.strftime("%Y-%m-%d %H%M"),
                    "end": current_time.strftime("%Y-%m-%d %H%M"),
                    "step": "MINUTE"
                },
                "traceState": "ALL",
                "queryOrder": "BY_START_TIME",
                "paging": {
                    "pageNum": 1,
                    "pageSize": 100
                },
                "minTraceDuration": None,
                "maxTraceDuration": None,
                "serviceId": service_id
            }
        }

        data: Optional[Dict[str, Any]] = self.visit_graphql(query, variables)

        traces: List[Dict[str, Any]] = data.get("data").get("data").get("traces")

        # 只有在需要保存的时候才进行保存
        if hasattr(self.config.config, 'traces_json_path'):
            with open(self.config.config['traces_json_path'], 'w', encoding='utf-8') as json_file:
                json.dump(traces, json_file, indent=4)
        return traces

    def query_single_trace(self, trace_id: str) -> List[Dict[str, Any]]:
        """
        查询单个追踪。
        :param trace_id: 追踪 ID
        :return: 追踪的链路
        """
        query: str = """
            query queryTrace($traceId: ID!) {
                trace: queryTrace(traceId: $traceId) {
                    spans {
                        traceId
                        segmentId
                        spanId
                        parentSpanId
                        refs {
                            traceId
                            parentSegmentId
                            parentSpanId
                            type
                        }
                        serviceCode
                        serviceInstanceName
                        startTime
                        endTime
                        endpointName
                        type
                        peer
                        component
                        isError
                        layer
                        tags {
                            key
                            value
                        }
                        logs {
                            time
                            data {
                                key
                                value
                            }
                        }
                        attachedEvents {
                            startTime {
                                seconds
                                nanos
                            }
                            event
                            endTime {
                                seconds
                                nanos
                            }
                            tags {
                                key
                                value
                            }
                            summary {
                                key
                                value
                            }
                        }
                    }
                }
            }
            """

        variables: Dict[str, str] = {
            "traceId": trace_id
        }

        data: Optional[dict[str, Any]] = self.visit_graphql(query, variables)

        chains: list[Dict[str, Any]] = data.get("data").get("trace").get("spans")
        return chains

    @staticmethod
    def get_node_attributes(chain_node: Dict[str, Any]) -> Optional[List[Dict[str, Any]]]:
        """
        获取节点属性。
        :param chain_node: 链路
        :return: 节点属性
        """
        forbidden_names: List[str] = ["HikariCP"]
        if chain_node["endpointName"].split("/")[0] in forbidden_names:
            return None
        endpoint_names: List[str] = [chain_node["endpointName"]]

        chain_type: str = "service"
        if chain_node["layer"] == "Database":
            chain_type = "database"
            tags: Dict[str, str] = {tag["key"]: tag["value"] for tag in chain_node["tags"]}
            sql: Optional[str] = tags.get("db.statement")
            if not sql:
                return None
            tables: List[str] = []
            try:
                normalized_sql = re.sub(r"%.*s", "?", sql)
                parsed_sqls: List[exp.Expression] = parse(normalized_sql, dialect=tags["db.type"])
            except Exception as e:
                print(f"error:{e}")
                return None
            for parsed_sql in parsed_sqls:
                tables += [table.this.sql() for table in parsed_sql.find_all(exp.Table)]
            if not tables:
                return None
            endpoint_names = [f'{tags["db.type"]}.{tags["db.instance"]}.{table}' for table in tables]
            if len(endpoint_names) == 0:
                return None

        keys: List[str] = ["traceId", "segmentId", "spanId", "parentSpanId", "startTime", "endTime"]
        nodes: List[Dict[str, Any]] = []
        for endpoint_name in endpoint_names:
            attributes: Dict[str, Any] = {"endpointName": endpoint_name, "type": chain_type}
            for key in keys:
                attributes[key] = chain_node[key]
            nodes.append(attributes)
        return nodes

    def tackle_map(self, chains: list[dict[str, Any]]) -> List[dict[str, Any]]:
        """
        绘制调用关系图。
        :param chains: 调用链路
        :return: 经过清洗之后的数据链路
        """
        simplified_chains: list[Dict[str, Any]] = []
        for chain in chains:
            nodes: Optional[list[Dict[str, Any]]] = self.get_node_attributes(chain)

            if not nodes:
                continue

            for node in nodes:
                node_id = node["endpointName"]
                simplified_chains.append(node)
                execution_time = node["endTime"] - node["startTime"]
                if self.graph_node_map.get(node_id):
                    self.graph_node_map[node_id].execution_time += execution_time
                    self.graph_node_map[node_id].count += 1
                else:
                    self.graph_node_map[node_id] = GraphNode(node_id, node["type"], execution_time, 1)
                if node['parentSpanId'] == -1:
                    self.traced_services.append(node_id)
                    continue

                else:
                    parent_node = next((n for n in chains if n["spanId"] == node["parentSpanId"]), None)
                    if parent_node:
                        parent_id = parent_node["endpointName"]
                        if self.graph_node_map.get(parent_id):
                            self.graph_node_map[node_id].father.add(parent_id)
                            self.graph_node_map[parent_id].children.add(node_id)
                        else:
                            self.graph_node_map[parent_id] = GraphNode(parent_id, parent_node["type"])
                            self.graph_node_map[node_id].father.add(parent_id)
                            self.graph_node_map[parent_id].children.add(node_id)

        return simplified_chains

    def create_map(self) -> None:
        """
        创建服务间调用关系图。
        :return: 无
        """
        self.services = self.get_services()

        for service in self.services:
            if service['label'] not in self.config.config['service_labels']:
                continue
            traces: list[dict[str, Any]] = self.query_traces(service['id'])
            for trace in traces:
                chains: list[dict[str, Any]] = self.query_single_trace(trace["traceIds"][0])
                for chain in self.tackle_map(chains):
                    if chain['traceId'] not in self.final_chains:
                        self.final_chains[chain['traceId']] = []
                    self.final_chains[chain['traceId']].append(chain)

    def save_call_chain(self) -> None:
        """
        保存调用链到文件。
        :return: 无
        """
        file_save_path: str = self.config.config['call_chains_json_path']
        with open(file_save_path, 'w', encoding='utf-8') as json_file:
            json.dump(self.final_chains, json_file, indent=4)

    def save_graph_node_map(self) -> None:
        """
        保存图节点映射到文件。
        :return: 无
        """
        file_save_path: str = self.config.config['graph_node_map_json_path']
        with open(file_save_path, 'w', encoding='utf-8') as json_file:
            json.dump({node_id: node.to_dict() for node_id, node in self.graph_node_map.items()}, json_file, indent=4)

    def save_traced_services(self) -> None:
        """
        保存已经追踪的服务到文件。
        :return: 无
        """
        file_save_path: str = self.config.config['traced_services_json_path']
        with open(file_save_path, 'w', encoding='utf-8') as json_file:
            json.dump(list(set(self.traced_services)), json_file, indent=4)

    def save_all(self):
        """
        保存所有数据
        :return: 无
        """
        self.save_call_chain()
        self.save_graph_node_map()
        self.save_traced_services()


def main():
    config_path = 'config.json'
    shutil.rmtree('./results/mapper_save_results', ignore_errors=True)
    os.makedirs('./results/mapper_save_results', exist_ok=True)
    tracer = SkywalkingTracer(config_path)
    tracer.create_map()
    tracer.save_all()


if __name__ == '__main__':
    main()
