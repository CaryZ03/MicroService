"""
gym: OpenAI Gym, providing Reinforcement Learning environments
numpy: NumPy, providing support for large, multi-dimensional arrays and matrices
DQNAgent: the DQNAgent class from DQN-demo/DQNAgent.py
"""
import gym
import numpy as np
from DQNAgent import DQNAgent

if __name__ == "__main__":
    # init the environment
    env = gym.make('CartPole-v1')
    state_size = env.observation_space.shape[0]
    action_size = env.action_space.n
    agent = DQNAgent(state_size, action_size)
    episodes = 1000
    batch_size = 32

    # train the agent
    for e in range(episodes):
        state = env.reset()
        state = state[0]
        
        # print("State before reshape:", state)
        # print("State type:", type(state))
        # print("State shape:", np.array(state).shape if isinstance(state, (list, np.ndarray)) else "Not an array")

        # reshape the state to fit the model
        state = np.reshape(state, [1, state_size])

        # each episode has 500 time steps
        for time in range(500):
            # choose action from current state
            action = agent.act(state)
            # execute the action
            next_state, reward, done, _, _ = env.step(action)

            reward = reward if not done else -10

            # print("Next state before reshape:", next_state)
            # print("Next state type:", type(next_state))
            # print("Next state shape:", np.array(next_state).shape if isinstance(next_state, (list, np.ndarray)) else "Not an array")
            # reshape the next state to fit the model
            next_state = np.reshape(next_state, [1, state_size])

            # put the experience in the memory
            agent.remember(state, action, reward, next_state, done)

            # the next state becomes the current state
            state = next_state

            if done:
                print(f"episode: {e}/{episodes}, score: {time}, e: {agent.epsilon:.2}")
                break

            if len(agent.memory) > batch_size:
                agent.replay(batch_size)
            
        if e % 50 == 0:
            agent.save(f"model_weights_{e}.h5")