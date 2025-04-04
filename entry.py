from mapper import buildGraph, showGraph
from Partition.partitioner import *

G = buildGraph(source='json', saveSpans=False, saveEntries=False)
showGraph(G)

stage2Main(G)

