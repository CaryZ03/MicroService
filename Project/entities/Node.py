class Node:
    def __init__(self, name: str, executionTime: int, count: int):
        self.name = name
        self.executionTime = executionTime
        self.count = count

    def get_node_name(self) -> str:
        return self.name
    
    def __str__(self) -> str:
        return "Node: " + self.name + \
                " executionTime: " + str(self.executionTime) + \
                " count: " + str(self.count)