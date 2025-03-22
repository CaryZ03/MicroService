"""
torch: PyTorch, an open source machine learning library
torch.optim: PyTorch's optimization module
torch.nn.functional: PyTorch's functional module
deque: Python's deque module, a double-ended queue
numpy: NumPy, a package for scientific computing with Python
random: Python's random module
"""
import torch
import torch.optim as optim
import torch.nn.functional as F
from collections import deque
import numpy as np
import random
from DQN import DQN


class DQNAgent:
    def __init__(self, state_size, action_size):
        self.state_size = state_size
        self.action_size = action_size
        # the experience replay memory to stock the experiences
        # (state, action, reward, next_state, done)
        self.memory = deque(maxlen=2000) 
        # discount rate
        self.gamma = 0.95   
        # exploration rate 
        self.epsilon = 1.0  
        self.epsilon_min = 0.01
        # the decay rate of the exploration rate
        self.epsilon_decay = 0.995
        # learning rate
        self.learning_rate = 0.001
        # the model of the agent. We use DQN here.
        self.model = DQN(state_size, action_size)
        self.optimizer = optim.Adam(self.model.parameters(), lr=self.learning_rate)

    # push in 'experience' to the memory
    def remember(self, state, action, reward, next_state, done):
        state = state.squeeze(0)  # 移除第0维的1
        next_state = next_state.squeeze(0)  # 移除第0维的1
        self.memory.append((state, action, reward, next_state, done))

    def act(self, state):
        # if the random number is smaller than the epsilon, we explore randomly
        if np.random.rand() <= self.epsilon:
            return random.randrange(self.action_size)
        # transform the state to a PyTorch tensor
        state = torch.FloatTensor(state)
        act_values = self.model(state)
        # return the action with the highest Q value
        return torch.argmax(act_values, dim = 1).item()
    
    # train the model
    def replay(self, batch_size):
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
        current_q = self.model(states).gather(1, actions.unsqueeze(1))
        # the max Q value of the next state
        next_q = self.model(next_states).detach().max(1)[0]
        # the target Q value
        target_q = rewards + self.gamma * next_q * (1 - dones)

        # calculate the loss and optimize the model
        loss = F.mse_loss(current_q.squeeze(), target_q)
        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()

        if self.epsilon > self.epsilon_min:
            self.epsilon *= self.epsilon_decay

    def save(self, path):
        torch.save(self.model.state_dict(), path)

    def load(self, path):
        self.model.load_state_dict(torch.load(path))