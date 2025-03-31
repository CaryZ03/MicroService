import random
import numpy as np
import torch
import torch.optim as optim
import torch.nn.functional as F
from collections import deque
from typing import Tuple, Deque 

from dqns.DQNetwork import DQNetwork

class DQNAgent:
    def __init__(self, state_dim: int, action_dim: int, hidden_dim: int=256,
                 learning_rate: float = 0.02, gamma: float = 0.99, 
                 epsilon: float = 5.0, epsilon_min : float= 1.0, 
                 epsilon_decay: float = 0.995):

        # the dimensions of the state, action, and hidden layers for DQNetwork.
        self.state_dim: int = state_dim
        self.action_dim: int = action_dim
        self.hidden_dim: int = hidden_dim

        print("set the dimension of DQNAgent successfully.")
        print("state dim: ", self.state_dim, "action dim: ", 
              self.action_dim, "hidden_dim: ", self.hidden_dim)
    
        # the hyperparameters for the Agent.
        self.learning_rate: float = learning_rate
        self.gamma: float = gamma
        self.epsilon: float = epsilon
        self.epsilon_min: float = epsilon_min
        self.epsilon_decay: float = epsilon_decay

        # the main and target DQNetwork.
        self.main_network: DQNetwork = DQNetwork(state_dim, hidden_dim, action_dim)
        self.target_network: DQNetwork = DQNetwork(state_dim, hidden_dim, action_dim)
        self.target_network.load_state_dict(self.main_network.state_dict())

        self.optimizer: optim.Adam = optim.Adam(self.main_network.parameters(), lr=learning_rate)

        self.memory: Deque[Tuple[np.ndarray, int, float, np.ndarray, bool]] = deque(maxlen=10000)

    # push in 'experience'
    def add_to_memory(self, state: np.ndarray, action: int, reward: float, 
                      next_state: np.ndarray, done: bool) -> None:
        self.memory.append((state, action, reward, next_state, done))

    def act(self, state: np.ndarray) -> int:
        # ensure state is a 2D tensor
        state_tensor: torch.Tensor = torch.FloatTensor(state)

        if state_tensor.ndim == 1:
            state_tensor = state_tensor.unsqueeze(0)

        if random.uniform(0, 100) < self.epsilon:
            return random.randint(0, self.action_dim - 1)
        else:
            with torch.no_grad():
                q_values: torch.Tensor = self.main_network(state_tensor)
                return torch.argmax(q_values).item()

    # train the model
    def train(self, batch_size: int) -> None:
        if len(self.memory) < batch_size:
            return

        # the experiences are sampled from the memory
        minibatch = random.sample(self.memory, batch_size)

        # we transform the experiences to PyTorch tensors
        states: torch.FloatTensor = torch.FloatTensor(np.array([i[0] for i in minibatch]))
        actions: torch.LongTensor = torch.LongTensor(np.array([i[1] for i in minibatch]))
        rewards: torch.FloatTensor = torch.FloatTensor(np.array([i[2] for i in minibatch]))
        next_states: torch.FloatTensor = torch.FloatTensor(np.array([i[3] for i in minibatch]))
        dones: torch.FloatTensor = torch.FloatTensor(np.array([i[4] for i in minibatch]))  # 0.0 or 1.0

        # the current state Q value
        current_q = self.main_network(states).gather(1, actions.unsqueeze(1))

        # the max Q value of the next state (standard DQN)
        next_q = self.target_network(next_states).max(1)[0].detach()

        # the target Q value
        target_q = rewards + self.gamma * next_q * (1 - dones)

        # calculate the loss and optimize the model
        loss = F.mse_loss(current_q.squeeze(), target_q)

        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()

        # update epsilon
        if self.epsilon > self.epsilon_min:
            self.epsilon *= self.epsilon_decay

    def update_target_network(self):
        # hard update target network
        self.target_network.load_state_dict(self.main_network.state_dict())

    def soft_update_target_network(self, tau=0.01):
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
