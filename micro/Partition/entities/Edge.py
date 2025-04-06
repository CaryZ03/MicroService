class Edge:
    def __init__(self, fromNode: int, toNode: int, weight: int):
        self.fromNode = fromNode
        self.toNode = toNode
        self.weight = weight

    def getTo(self) -> int:
        return self.toNode
    
    def getWeight(self) -> int:
        return self.weight