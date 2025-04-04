import json

import networkx as nx
import matplotlib.pyplot as plt

from mapper import Mapper



# 创建有向图
G = nx.DiGraph()

# entries = []

# 读取 JSON 文件
with open("entries_demo.json", "r") as json_file:
    entries = json.load(json_file)
    
print("Entries:", entries)

spanss = []

# services = queryServices()
# # print(json.dumps(services, indent=1))
# for service in services:
#     traces = queryTraces(service["id"])
#     # print(json.dumps(traces, indent=1))
#     for trace in traces:
#         spans = queryTrace(trace["traceIds"][0])
#         spanss.append(spans)
#         addToGraph(G, spans, entries)
#     break

# with open("spans.json", "w") as outfile:
#     json.dump(spanss, outfile, indent=4)


with open("spans.json", "r") as infile:
    spanss = json.load(infile)

# 打印读取的数据
print(spanss)

for spans in spanss:
    addToGraph(G, spans, entries)

nx.write_graphml(G, "demo-with-weight.graphml")

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
# with open("entries_demo.json", "w") as json_file:
#     json.dump(entries, json_file, indent=4)  # 使用 indent 参数美化输出