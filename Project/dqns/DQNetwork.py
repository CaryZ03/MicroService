import torch
import torch.nn as nn
import torch.nn.functional as F

class DQNetwork(nn.Module):
    def __init__(self, input_dim: int, hidden_dim: int, output_dim: int):
        super(DQNetwork, self).__init__()
        self.fc1: nn.Linear = nn.Linear(input_dim, hidden_dim)
        self.fc2: nn.Linear = nn.Linear(hidden_dim, hidden_dim)
        self.fc3: nn.Linear = nn.Linear(hidden_dim, output_dim)

    def forward(self, x: torch.Tensor) ->torch.Tensor:
        x: torch.Tensor = F.relu(self.fc1(x))
        x: torch.Tensor = F.relu(self.fc2(x))
        return self.fc3(x)