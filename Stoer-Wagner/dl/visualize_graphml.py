import networkx as nx
from graphviz import Source

# Read GraphML file
graph = nx.read_graphml("demo-with-weight.graphml")

# Remove "\"" characters from node names with relabeling
mapping = {node: node.replace(":", "_") for node in graph.nodes()}
graph = nx.relabel_nodes(graph, mapping)

# Convert to pydot graph and adjust global graph attributes
pd_graph = nx.nx_pydot.to_pydot(graph)
pd_graph.set_dpi("300")       # Set image resolution
pd_graph.set("overlap", "false")  # Prevent overlapping nodes

# Convert updated pydot graph to DOT string
dot_str = pd_graph.to_string()

# Use Graphviz 'neato' engine for a force-directed layout
source = Source(dot_str, engine="neato")
source.render("graph_output", format="png", view=True)  # Saves and views the PNG