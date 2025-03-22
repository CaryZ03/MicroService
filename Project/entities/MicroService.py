class MicroService:
    def __init__(self, id):
        self.nodes = {}
        self.id = id
        self.setStatus("free")

    def setStatus(self, status) -> None:
        self.status = status

    def getStatus(self) -> str:
        return self.status

    def addNode(self, node) -> None:
        self.nodes[node.name] = node

    def findNode(self, nodeName):
        return self.nodes.get(nodeName, None)
    
    def delCertainNode(self, nodeName):
        if nodeName in self.nodes:
            del self.nodes[nodeName]

    def delNodes(self):
        self.nodes.clear()

    def getNodes(self):
        return self.nodes.values()
    
    def getNodeCount(self):
        return len(self.nodes)
    
    def __str__(self):
        return "microservice: " + str(self.id) + \
            " status: " + self.status
    
    def printCertainNode(self, nodeName):
        print(self.nodes[nodeName])
    
    def printNodes(self):
        for node in self.nodes.values():
            print(node)