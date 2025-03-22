"""
torch.nn: PyTorch's neural network module
torch.nn.functional: PyTorch's functional module

"""
import torch.nn as nn
import torch.nn.functional as F

class DQN(nn.Module):
    def __init__(self, state_size, action_size):
        super(DQN, self).__init__()
        # we only create 3 full connected layers.
        self.fc1 = nn.Linear(state_size, 24)
        self.fc2 = nn.Linear(24, 24)
        self.fc3 = nn.Linear(24, action_size)

    def forward(self, x):
        # we use relu here instead of sigmoid.
        x= F.relu(self.fc1(x))
        x= F.relu(self.fc2(x))
        return self.fc3(x)