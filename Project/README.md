# Introducition

This is our DQN-based reinforcement learning model that can partition different functions into appropriate microservice. 

The entrypoint is `Main.py`, and `stage1Main()` doesn't use DQN, while `stage2Main()` does.

You just need to put your functions calling graph at `data/src`, and you will get the target graph at `data/target`.

# Problem

If you use DQN, it is much more likely to partition ALL function in **ONE microservice** , which is not we want.