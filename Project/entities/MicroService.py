class MicroService:
    def __init__(self, id):
        self.id: int = id
        self.status: str = "free"

    def setStatus(self, status: str) -> None:
        self.status = status

    def getStatus(self) -> str:
        return self.status
    
    def __str__(self) -> str:
        return "microservice: " + str(self.id) + \
            " status: " + self.status