class Node:
    def __init__(self, name, executionTime, count):
        self.name = name
        self.executionTime = executionTime
        self.count = count
        self.msID = -1

    def setMicroServiceID(self, msID):
        self.msID = msID
    
    def getMicroServiceID(self):
        return self.msID
    
    def __str__(self):
        return "Node: " + self.name + \
                " executionTime: " + str(self.executionTime) + \
                " count: " + str(self.count)