import numpy as np

class Environment:
    def __init__(self, gridSize, normalReward = 0):
        self.gridSize = gridSize
        # [0, 1]: right; [0, -1]: left; [1, 0]: down; [-1, 0]: up
        self.actions = [[0, 1], [0, -1], [1, 0], [-1, 0]]
        self.numActions = len(self.actions)

        self.rewards = np.array([[normalReward] * gridSize for _ in range(gridSize)])

    def setGoal(self, goals, goalRewards):
        self.goals = goals
        for i in range(len(goals)):
            self.rewards[goals[i][0]][goals[i][1]] = goalRewards[i]

    def setPunish(self, punishs, punishRewards):
        self.punishs = punishs
        for i in range(len(punishs)):
            self.rewards[punishs[i][0]][punishs[i][1]] = punishRewards[i]

    def getReward(self, state):
        return self.rewards[state[0]][state[1]]
    
    def move(self, state, action):
        nsx = state[0] + self.actions[action][0]
        nsy = state[1] + self.actions[action][1]

        nsx = min(self.gridSize - 1, nsx)
        nsx = max(0, nsx)
        nsy = min(self.gridSize - 1, nsy)
        nsy = max(0, nsy)

        return [nsx, nsy]