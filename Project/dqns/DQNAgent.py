import random
import numpy as np
import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
from collections import deque
from typing import Tuple, Deque 

from .DQNetwork import DQNetwork

class DQNAgent:
    def __init__(self, state_dim: int, action_dim: int, hidden_dim: int = 64, 
                 learning_rate: float = 0.001, gamma: float = 0.99, 
                 epsilon: float = 1.0, epsilon_min : float= 0.01, 
                 epsilon_decay: float = 0.995):
        
        self.state_dim: int = state_dim
        self.action_dim: int = action_dim
        self.hidden_dim: int = hidden_dim

        self.learning_rate: float = learning_rate
        self.gamma: float = gamma
        self.epsilon: float = epsilon
        self.epsilon_min: float = epsilon_min
        self.epsilon_decay: float = epsilon_decay
        
        self.main_network: DQNetwork = DQNetwork(state_dim, hidden_dim, action_dim)
        self.target_network: DQNetwork = DQNetwork(state_dim, hidden_dim, action_dim)
        self.target_network.load_state_dict(self.main_network.state_dict())
        
        self.optimizer: optim.Adam = optim.Adam(self.main_network.parameters(), lr=learning_rate)
        
        self.memory: Deque[Tuple[np.ndarray, int, float, np.ndarray, bool]] = deque(maxlen=10000)

    # push in 'experience' to the memory
    def add_to_memory(self, state, action, reward, next_state, done):
        self.memory.append((state, action, reward, next_state, done))

    def act(self, state: np.ndarray, num_communities: int) -> int:
        # 动态调整 epsilon
        if num_communities < 2:
            epsilon: float = max(self.epsilon_min, 0.8)
        elif num_communities > 10:
            epsilon: float = max(self.epsilon_min, 0.2)
        else:
            epsilon: float = self.epsilon
        
        if random.random() < epsilon:
            return random.randint(0, self.action_dim - 1)
        else:
            with torch.no_grad():
                state_tensor: torch.Tensor = torch.FloatTensor(state)
                q_values: torch.Tensor = self.main_network(state_tensor)
                return torch.argmax(q_values).item()
    
    # train the model
    def train(self, batch_size) -> None:
        if len(self.memory) < batch_size:
            return
        # the experiences are sampled from the memory
        minibatch = random.sample(self.memory, batch_size)

        # we transform the experiences to PyTorch tensors
        states = torch.FloatTensor(np.array([i[0] for i in minibatch]))
        actions = torch.LongTensor(np.array([i[1] for i in minibatch]))
        rewards = torch.FloatTensor(np.array([i[2] for i in minibatch]))
        next_states = torch.FloatTensor(np.array([i[3] for i in minibatch]))
        dones = torch.FloatTensor(np.array([i[4] for i in minibatch]))

        # print("States shape:", states.shape)  # 应该是 [batch_size, state_size]
        # print("Actions shape:", actions.shape)  # 应该是 [batch_size]

        # the current state Q value
        current_q = self.main_network(states).gather(1, actions.unsqueeze(1))
        # the max Q value of the next state
        next_q = self.target_network(next_states).max(1)[0].detach()
        # the target Q value
        target_q = rewards + self.gamma * next_q * (1 - dones)

        # calculate the loss and optimize the model
        loss = F.mse_loss(current_q.squeeze(), target_q)

        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()

        if self.epsilon > self.epsilon_min:
            self.epsilon *= self.epsilon_decay

    def update_target_network(self):
        self.target_network.load_state_dict(self.main_network.state_dict())