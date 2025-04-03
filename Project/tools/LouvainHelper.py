import networkx as nx
from typing import List, Dict
from community import community_louvain

class LouvainHelper:
    def __init__(self):
        pass

    def partitionCommunities(self, graph: nx.DiGraph) -> Dict[str, int]:
        """
        use Louvain algorithm to do Community finding.
        : param Graph: the Directed Graph Object.
        : return: the partition of community in Dict[node, communityID]

        """

        # transfer the graph into undirected graph since Louvain Algorithm
        # can ONLY be applied on undirected graph.
        graphUndirected : nx.Graph = graph.to_undirected()
        # do partition.
        partition: Dict[str, int] = community_louvain.best_partition(graphUndirected)

        return partition
    
    def partitionMicroservices(self, partition: Dict[str, int]) -> Dict[int, List[str]]:
        """
        divide microserives according to the partition result of communities.
        :param partition: the partition result of communities, Dict: {node, communityID}
        :return: partition result of microservices, Dict: {communityID: nodeList}
        """
        microservices: Dict[int, List[str]] = {}

        for node, communityID in partition.items():
            if communityID not in microservices:
                microservices[communityID] = []
            microservices[communityID].append(node)

        return microservices

    def convertMicroservicesToGraph(self, callGraph: nx.DiGraph, microservices: Dict[int, List[str]]) -> nx.DiGraph:
        microservicesgraph : nx.DiGraph = nx.DiGraph()

        for serviceID in microservices:
            microservicesgraph.add_node(serviceID)

        for source, target, _ in callGraph.edges(data = True):
            # find the microservices source and target funtions belong to.
            sourceService: int = next(ServiceId for ServiceId, Functions in microservices.items() if source in Functions)
            targetService: int = next(ServiceId for ServiceId, Functions in microservices.items() if target in Functions)

            # if the source and the target microservice is not the same, we will add an edge.
            if sourceService != targetService:
                if microservicesgraph.has_edge(sourceService, targetService):
                    continue
                else:
                    microservicesgraph.add_edge(sourceService, targetService)

        return microservicesgraph