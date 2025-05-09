# Introducition

This is our DQN-based reinforcement learning model that can partition different functions into appropriate microservice. 

The entrypoint is `Main.py`, and `stage1Main()` uses community louvain to do microservice partition, while `stage2Main()` uses DQN.

You just need to put your functions calling graph at `data/src`, and you will get the target graph at `data/target`.

# Problem

DQN is much slower and the result is not satisfying. Before we find a highly effective mathematic method, it is recommended to use louvain instead of DQN.