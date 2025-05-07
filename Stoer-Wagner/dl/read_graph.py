import networkx as nx
import numpy as np

def parse_graphml(file_path):
    # Load the graph from the GraphML file.
    G = nx.read_graphml(file_path)

    # Get a list of nodes and create a mapping for indices.
    nodes = list(G.nodes())
    node_to_idx = {node: i for i, node in enumerate(nodes)}

    # Create an empty NumPy matrix.
    n = len(nodes)
    adj_matrix = np.zeros((n, n))

    # Iterate over edges to fill the matrix.
    for u, v, data in G.edges(data=True):
        weight = float(data.get("weight", 1))
        i = node_to_idx[u]
        j = node_to_idx[v]
        adj_matrix[i, j] = weight
        # If the graph is undirected, assign the symmetric value.
        if not G.is_directed():
            adj_matrix[j, i] = weight

    return nodes, adj_matrix

if __name__ == "__main__":
    file_path = "./demo-with-weight.graphml"
    nodes, numpy_matrix = parse_graphml(file_path)

    print("Extracted Nodes:")
    for node in nodes:
        print(node)

    print("\nAdjacency Matrix (edge weights):")
    print(numpy_matrix)

    np.save('./call_matrix.npy', numpy_matrix)