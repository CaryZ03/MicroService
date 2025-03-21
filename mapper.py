import requests
import json
import re

import networkx as nx
import matplotlib.pyplot as plt
from datetime import datetime, timedelta
from sqlglot import parse, parse_one, exp

def queryGraphql(query, variables):
    # 设置请求的 URL 和头部
    url = "http://localhost:8081/graphql"  # 替换为你的 GraphQL 服务端点
    headers = {
        "Content-Type": "application/json",
    }

    # 发送 POST 请求
    response = requests.post(url, json={"query": query, "variables": variables}, headers=headers)

    # 检查响应
    if response.status_code == 200:
        data = response.json()
    else:
        print("Error:", response.status_code, response.text)
        return None
        
    # print(json.dumps(data, indent=1))
    return data

def queryServices():
    # 定义 GraphQL 查询
    query = """
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

    variables = {
        "layer":"GENERAL"
    }

    data = queryGraphql(query, variables)

    services = data.get("data").get("services")
    # print("services:")
    # print(json.dumps(services, indent=1))
    return services

def queryTraces(serviceId):
    # 定义 GraphQL 查询
    query = """
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
    
    time_gap = timedelta(minutes=300)
    current_time = datetime.now()
    former_time = current_time - time_gap
    
    variables = {
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
            "serviceId": serviceId
        }
    }

    data = queryGraphql(query, variables)

    traces = data.get("data").get("data").get("traces")
    # print("traces:")
    # print(json.dumps(traces, indent=1))
    return traces

def queryTrace(traceId):
    # 定义 GraphQL 查询
    query = """
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
    
    variables = {
        "traceId": traceId
    }

    data = queryGraphql(query, variables)

    spans = data.get("data").get("trace").get("spans")
    # print("spans:")
    # print(json.dumps(spans, indent=1))
    return spans


def getAttributes(span):
    forbid_names = ["HikariCP"]
    if span["endpointName"].split("/")[0] in forbid_names:
        return None
    endpointNames = [span["endpointName"]]
    type = "service"
    if span["layer"] == "Database":
        type = "database"
        tags = {tag["key"]: tag["value"] for tag in span["tags"]}
        sql = tags["db.statement"]
        if not sql:
            return None
        # print(json.dumps(span, indent=1))
        tables = []
        try:
            parsed_sqls = parse(sql)
        except Exception as e:
            print(e)
            return None
        for parsed_sql in parsed_sqls:
            # print(parsed_sql.sql(pretty=True))
            tables += [table.this.sql() for table in parsed_sql.find_all(exp.Table)]
            # print("Tables:", tables)
        if not tables:
            return None
        endpointNames = [f'{tags["db.type"]}.{tags["db.instance"]}.{table}' for table in tables]
        # print("EndpointNames:", endpointNames)
        
    keys = ["traceId", "segmentId", "spanId", "parentSpanId", "startTime", "endTime"]
    nodes = []
    for endpointName in endpointNames:
        attributes = {}
        attributes["endpointName"] = endpointName
        attributes["type"] = type
        for key in keys:
            attributes[key] = span[key]
        # print(json.dumps(attributes, indent=1))
        nodes += [attributes]
    return nodes


def toRegex(name):
    # print("name:", name)
    patterns = [
        {
            "var": r"{int}",
            "regex": r"\\d+"
        }
    ]
    
    for pattern in patterns:
        name = re.sub(pattern["var"], pattern["regex"], name)
    return name
    
    
def matchEntry(name, entries):
    tmp_entry = {}
    for entry in entries:
        if entry["name"] == name:
            return entry
        elif re.fullmatch(toRegex(entry["name"]), name):
            # print("hihi!")
            tmp_entry = entry
    return tmp_entry


def getNodeId(node):
    node_id = node["endpointName"]
    if node["spanId"] == 0:
        entry = matchEntry(node_id, entries)
        node_id = entry["name"] if entry else node_id
    return node_id

    


def addToGraph(G, spans, entries):
    weight = 1
    for span in spans:
        nodes = getAttributes(span)
        if not nodes:
            continue
        
        for node in nodes:
            node_id = getNodeId(node)
            execution_time = node["endTime"] - node["startTime"]
            
            if G.has_node(node_id):
                G.nodes[node_id]["execution_time"] += execution_time
                G.nodes[node_id]["count"] += 1
            else:
                G.add_node(node_id, execution_time=execution_time, count=1)

            # 如果 parentSpanId 不是 -1，则添加边
            if node["parentSpanId"] != -1:
                parent_node = next((n for n in spans if n["spanId"] == node["parentSpanId"]), None)
                if parent_node:
                    parent_node_id = getNodeId(parent_node)
                    if G.has_edge(node_id, parent_node_id):
                        G[node_id][parent_node_id]["weight"] += weight
                    else:
                        G.add_edge(node_id, parent_node_id, weight=weight)
            else:
                # entries += [node]
                name = node["endpointName"]
                entry = matchEntry(name, entries)
                # print("Name:", name)
                # print("Entry:", entry)
                weight = entry["weight"] if entry else 1
                # print("Weight:", weight)
                
    
    
# 创建有向图
G = nx.DiGraph()

# 读取 JSON 文件
with open("entries.json", "r") as json_file:
    entries = json.load(json_file)
    
# entries = []
print("Entries:", entries)

services = queryServices()
# print(json.dumps(services, indent=1))
for service in services:
    traces = queryTraces(service["id"])
    # print(json.dumps(traces, indent=1))
    for trace in traces:
        spans = queryTrace(trace["traceIds"][0])
        addToGraph(G, spans, entries)
    break

# print(json.dumps(nodes, indent=1))

# nx.write_graphml(G, "graph-without-hikari.graphml")

# import networkx as nx
# G = nx.read_graphml("microservice_graph-without-hikari.graphml")

# microservice_
# 绘制图
pos = nx.spring_layout(G)
# , k=0.15, iterations=20
nx.draw(G, pos, with_labels=True, node_size=300, node_color="skyblue", font_size=5, font_weight="bold", arrows=True)

# # 添加节点标签
# labels = nx.get_node_attributes(G, 'execution_time')
# nx.draw_networkx_labels(G, pos, labels=labels, font_size=5)

# 添加边权重标签
edge_labels = nx.get_edge_attributes(G, 'weight')
nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=5)

# 显示图
plt.title("Trace Dependency Graph")
plt.show()


# entry_names = list(set([entry["endpointName"] for entry in entries]))
# print("Entries:", entry_names)

# entries = [{"name": entry_name, "weight": 1} for entry_name in entry_names]

# # 写入 JSON 文件
# with open("entries.json", "w") as json_file:
#     json.dump(entries, json_file, indent=4)  # 使用 indent 参数美化输出
        
        

