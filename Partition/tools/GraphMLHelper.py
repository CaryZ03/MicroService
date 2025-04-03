import os
import networkx as nx
import matplotlib.pyplot as plt
from typing import List, Dict, Tuple


class GraphMLHelper:
    def __init__(self):
        pass

    def getGraphMLPath(self, basePath: str) -> List[str]:
        """
        get the .ml path strings.
        """
        graphMLPaths : List[str] = []

        for root, _, files in os.walk(basePath):
            for file in files:
                graphMLPath: str = os.path.join(root, file)
                graphMLPaths.append(graphMLPath)

        return graphMLPaths
    
    def getGraphFromGraphML(self, graphPath: str) -> nx.DiGraph:
        graph : nx.DiGraph = nx.read_graphml(graphPath)
        return graph
    
    def visualizeGraph(self, graph: nx.DiGraph, title: str) -> None:
        """
        visualize the graph.
        : param Graph: the Directed Graph Object.
        : param Title: the String Object(title of the graph.)
        """
        # 1. calculate the layout of the nodes.
        Position: Dict[str, Tuple[float, float]] = nx.spring_layout(graph)

        # 2. draw nodes and edges.
        nx.draw(graph, Position, with_labels=True, node_color="lightblue", node_size=2000, font_size=10, font_weight="bold")

        # 3. if the edges have weight, draw it.
        if graph.edges:
            EdgeLabels: Dict[Tuple[str, str], int] = nx.get_edge_attributes(graph, "weight")
            nx.draw_networkx_edge_labels(graph, Position, edge_labels=EdgeLabels)

        # 4. show the graph.
        plt.title(title)
        plt.show()

    def saveGraphAsGraphML(self, graph: nx.DiGraph, filepath: str) -> None:
        nx.write_graphml(graph, filepath)
        print(f"The graph has been saved as {filepath}")