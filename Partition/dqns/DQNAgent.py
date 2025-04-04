import random
import numpy as np
import torch
import torch.optim as optim
import torch.nn.functional as F
from collections import deque
from typing import Tuple, Deque 

from .DQNetwork import DQNetwork

class DQNAgent:
    def __init__(self, state_dim: int, action_dim: int,
                 learning_rate: float = 0.0001, gamma: float = 0.99, 
                 epsilon: float = 0.75, epsilon_min : float= 0.05, 
                 epsilon_decay: float = 0.995):

        # the dimensions of the state and action for DQNetwork.
        self.state_dim: int = state_dim
        self.action_dim: int = action_dim

        print("set the dimension of DQNAgent successfully.")
        print("state dim: ", self.state_dim, "action dim: ",  self.action_dim)
    
        # the hyperparameters for the Agent.
        self.learning_rate: float = learning_rate
        self.gamma: float = gamma
        self.epsilon: float = epsilon
        self.epsilon_min: float = epsilon_min
        self.epsilon_decay: float = epsilon_decay

        # the main and target DQNetwork.
        self.main_network: DQNetwork = DQNetwork(state_dim, action_dim)
        self.target_network: DQNetwork = DQNetwork(state_dim, action_dim)
        self.target_network.load_state_dict(self.main_network.state_dict())
        self.optimizer: optim.Adam = optim.Adam(self.main_network.parameters(), lr=learning_rate)

        self.memory: Deque[Tuple[np.ndarray, int, float, np.ndarray, bool]] = deque(maxlen=10000)

    # push in 'experience'
    def add_to_memory(self, state: np.ndarray, action: int, reward: float, 
                      next_state: np.ndarray, done: bool) -> None:
        self.memory.append((state, action, reward, next_state, done))

    def act(self, state: np.ndarray) -> int:
        # print(state)
        state_tensor: torch.tensor = torch.tensor(state, dtype=torch.float32)

        if state_tensor.ndim == 1:
            state_tensor = state_tensor.unsqueeze(0)

        if random.random() < self.epsilon:
            return random.randint(0, self.action_dim - 1)
        else:
            with torch.no_grad():
                q_values: torch.Tensor = self.main_network(state_tensor)
                return torch.argmax(q_values).item()

    def train(self, batch_size: int) -> None:
        if len(self.memory) < batch_size:
            return

        minibatch = random.sample(self.memory, batch_size)

        states = torch.tensor(np.array([i[0] for i in minibatch]), dtype=torch.float32)
        actions = torch.tensor(np.array([i[1] for i in minibatch]), dtype=torch.long)
        rewards = torch.tensor(np.array([i[2] for i in minibatch]), dtype=torch.float32)
        next_states = torch.tensor(np.array([i[3] for i in minibatch]), dtype=torch.float32)
        dones = torch.tensor(np.array([i[4] for i in minibatch]), dtype=torch.bool)

        # 计算当前 Q 值
        current_q = self.main_network(states).gather(1, actions.unsqueeze(1)).squeeze(1)
        # 计算下一状态的 Q 值
        next_q = self.target_network(next_states).max(1)[0].detach()
        # 计算目标 Q 值
        target_q = rewards + self.gamma * next_q * (~dones)

        # 计算损失并更新网络
        self.optimizer.zero_grad()
        loss = F.mse_loss(current_q, target_q)
        loss.backward()
        self.optimizer.step()

        # 打印损失值
        # print("Loss:", loss.item())

        # 更新 epsilon
        if self.epsilon > self.epsilon_min:
            self.epsilon *= self.epsilon_decay

    def updateTargetNetwork(self):
        # hard update target network
        self.target_network.load_state_dict(self.main_network.state_dict())

    def softUpdateTargetNetwork(self, tau=0.01):
        # soft update target network
        for target_param, main_param in zip(self.target_network.parameters(), self.main_network.parameters()):
            target_param.data.copy_(tau * main_param.data + (1.0 - tau) * target_param.data)

    def save_model(self, path: str):
        # save the model to file
        torch.save(self.main_network.state_dict(), path)

    def load_model(self, path: str):
        # load the model from file
        self.main_network.load_state_dict(torch.load(path))
        self.target_network.load_state_dict(self.main_network.state_dict())
