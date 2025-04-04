import torch
import torch.nn as nn

class DQNetwork(nn.Module):
    def __init__(self, input_dim: int, output_dim: int):
        super(DQNetwork, self).__init__()
        self.network: nn.Sequential = nn.Sequential(
            nn.Linear(input_dim, 128),
            nn.ReLU(),
            nn.Linear(128, 128),
            nn.ReLU(),
            nn.Linear(128, 64),
            nn.ReLU(),
            nn.Linear(64, output_dim)
        )

    def forward(self, x: torch.Tensor) ->torch.Tensor:
        return self.network(x)