import numpy as np

class Agent:
    def __init__(self, env, lr = 0.1, gamma = 0.9, epsilon = 0.1):
        self.env = env
        self.lr = lr
        self.gamma = gamma
        self.epsilon = epsilon
        self.Q = np.zeros((env.gridSize, env.gridSize, env.numActions))

    def chooseAction(self, state):
        if np.random.rand() < self.epsilon:
            return np.random.choice(self.env.numActions)
        else:
            return np.argmax(self.Q[state[0], state[1], :])
        
    def updateQ(self, state, action, reward, nextState):
        # 注意是+=.
        self.Q[state[0], state[1], action] += self.lr * (
            reward + self.gamma * np.max(self.Q[nextState[0], nextState[1], :]) - self.Q[state[0], state[1], action]
        )

    def train(self, numEpisodes = 1000):
        for _ in range(numEpisodes):
            # 默认起点位置是[0, 0]
            state = [0, 0]
            while state not in self.env.goals:
                action = self.chooseAction(state)
                nextState = self.env.move(state, action)
                reward = self.env.getReward(nextState)
                self.updateQ(state, action, reward, nextState)
                state = nextState

    def getOptimalPath(self):
        state = (0, 0)
        optimalPath = [state]
        while state not in self.env.goals:
            action = np.argmax(self.Q[state[0], state[1], :])
            state = self.env.move(state, action)
            optimalPath.append(state)
        return optimalPath