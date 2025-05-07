import networkx as nx
import matplotlib.pyplot as plt

# 你的图数据
graph_data = {
    "service_len": 23,
    "edge_len": 16,
    "all_services": [
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.get_all_products.25",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.get_user.18",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.get_all_users.25",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.get_order.23",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.get_all_orders.30",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.create_user.10",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.get_all_products.25",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.create_user.11",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.tmp.testcase.4",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.get_all_users.25",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.create_product.11",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.models.user_model.__init__.7",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.get_user.19",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.utils.database.init_db.10",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.create_order.15",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.models.product_model.__init__.9",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.create_product.10",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_all_orders.25",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.create_order.11",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.get_product.18",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.get_product.19",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.models.order_model.__init__.8",
        "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_order.19"
    ],
    "graph_pairs": [
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.get_user.18",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.get_user.19",
            "dataTransmitted": 1776.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.get_user.19",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.tmp.testcase.4",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.get_product.18",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.get_product.19",
            "dataTransmitted": 1776.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.get_product.19",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.tmp.testcase.4",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.get_order.23",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_order.19",
            "dataTransmitted": 1776.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_order.19",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.tmp.testcase.4",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.get_all_orders.30",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_all_orders.25",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.get_all_orders.25",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.tmp.testcase.4",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.get_all_products.25",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.get_all_products.25",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.get_all_users.25",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.get_all_users.25",
            "dataTransmitted": 1712.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.order_controller.create_order.15",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.create_order.11",
            "dataTransmitted": 1848.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.order_service.create_order.11",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.models.order_model.__init__.8",
            "dataTransmitted": 2304.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.product_controller.create_product.10",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.create_product.11",
            "dataTransmitted": 1760.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.product_service.create_product.11",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.models.product_model.__init__.9",
            "dataTransmitted": 2144.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.controllers.user_controller.create_user.10",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.create_user.11",
            "dataTransmitted": 1864.0
        },
        {
            "source": "Users.xht.Desktop.split-microservice.examples.flask_demo.services.user_service.create_user.11",
            "target": "Users.xht.Desktop.split-microservice.examples.flask_demo.models.user_model.__init__.7",
            "dataTransmitted": 2256.0
        }
    ]
}

# 创建有向图（无向图可以用 nx.Graph()）
G = nx.Graph()

# 添加节点
G.add_nodes_from(graph_data["all_services"])

# 添加边（带权重）
for edge in graph_data["graph_pairs"]:
    G.add_edge(edge["source"], edge["target"], weight=edge["dataTransmitted"])

# 绘制图形
plt.figure(figsize=(28, 26))

# 节点布局（spring_layout 是一种自动调整的布局方式）
pos = nx.spring_layout(G)

# 绘制节点
nx.draw_networkx_nodes(G, pos, node_size=700, node_color='lightblue')

# 绘制边（带权重）
nx.draw_networkx_edges(G, pos, width=2, edge_color='gray')

# 绘制节点标签
nx.draw_networkx_labels(G, pos, font_size=12, font_weight='bold')

# 绘制边的权重
edge_labels = nx.get_edge_attributes(G, 'weight')
nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels)

# 显示图形
plt.title("Graph Visualization")
plt.axis('off')  # 关闭坐标轴
plt.show()