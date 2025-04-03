from Environment import Environment
from Agent import Agent

if __name__ == "__main__":
    # first step: create an environment.
    gridSize = 4
    goals = [[3, 3]]
    goalRewards = [10]
    punishs = [[0, 1], [0, 2], [0, 3], [1, 1], [1, 2], [1, 3], [3, 0], [3, 1], [3, 2]]
    punishRewards = [-10] * len(punishs)
    normalReward = 0
    env = Environment(gridSize, normalReward)
    env.setGoal(goals, goalRewards)
    env.setPunish(punishs, punishRewards)
   # print(env.rewards)

    # second step: create the agent.
    lr = 0.1
    gamma = 0.9
    epsilon = 0.1
    agent = Agent(env, lr, gamma, epsilon)

    # third step: start training!!
    agent.train(1000)

    # four step: print the optimal path.
    print("Final Q-table:")
    print(agent.Q)
    print("Optimal path:", agent.getOptimalPath())