import numpy as np
from sklearn.cluster import SpectralClustering
from sklearn.metrics import silhouette_score
from stable_baselines3 import PPO
from gym import Env
from gym import spaces

sim_matrix = np.load("../results/dependence/mono2micro_matrix.npy")  # 假设已加载相似度矩阵
names = [
    {
        "func_name": "Config",
        "file_path": "examples/flask_demo/config",
        "lineno": 25,
        "end_lineno": 28,
        "execution_time": 0,
        "count": 0,
        "label": 5
    },
    {
        "func_name": "Product",
        "file_path": "examples/flask_demo/models/product_model",
        "lineno": 7,
        "end_lineno": 27,
        "execution_time": 0,
        "count": 1,
        "label": 2
    },
    {
        "func_name": "OrderService",
        "file_path": "examples/flask_demo/services/order_service",
        "lineno": 10,
        "end_lineno": 29,
        "execution_time": 89,
        "count": 5,
        "label": 1
    },
    {
        "func_name": "main",
        "file_path": "examples/flask_demo/app",
        "lineno": 33,
        "end_lineno": 38,
        "execution_time": 54409,
        "count": 2,
        "label": 7
    },
    {
        "func_name": "UserController",
        "file_path": "examples/flask_demo/controllers/user_controller",
        "lineno": 7,
        "end_lineno": 30,
        "execution_time": 116,
        "count": 5,
        "label": 0
    },
    {
        "func_name": "patch_trace",
        "file_path": "examples/flask_demo/patch_trace",
        "lineno": 10,
        "end_lineno": 63,
        "execution_time": 0,
        "count": 0,
        "label": 6
    },
    {
        "func_name": "ProductController",
        "file_path": "examples/flask_demo/controllers/product_controller",
        "lineno": 7,
        "end_lineno": 30,
        "execution_time": 104,
        "count": 5,
        "label": 2
    },
    {
        "func_name": "User",
        "file_path": "examples/flask_demo/models/user_model",
        "lineno": 6,
        "end_lineno": 22,
        "execution_time": 0,
        "count": 1,
        "label": 0
    },
    {
        "func_name": "testcase",
        "file_path": "examples/flask_demo/tmp",
        "lineno": 4,
        "end_lineno": 6,
        "execution_time": 0,
        "count": 8,
        "label": 8
    },
    {
        "func_name": "ProductService",
        "file_path": "examples/flask_demo/services/product_service",
        "lineno": 10,
        "end_lineno": 28,
        "execution_time": 90,
        "count": 5,
        "label": 2
    },
    {
        "func_name": "Weight",
        "file_path": "examples/flask_demo/controllers/order_controller",
        "lineno": 8,
        "end_lineno": 9,
        "execution_time": 0,
        "count": 0,
        "label": 4
    },
    {
        "func_name": "OrderController",
        "file_path": "examples/flask_demo/controllers/order_controller",
        "lineno": 12,
        "end_lineno": 35,
        "execution_time": 102,
        "count": 5,
        "label": 1
    },
    {
        "func_name": "DatabaseManager",
        "file_path": "examples/flask_demo/utils/database",
        "lineno": 7,
        "end_lineno": 14,
        "execution_time": 2,
        "count": 2,
        "label": 3
    },
    {
        "func_name": "Order",
        "file_path": "examples/flask_demo/models/order_model",
        "lineno": 7,
        "end_lineno": 28,
        "execution_time": 0,
        "count": 1,
        "label": 1
    },
    {
        "func_name": "UserService",
        "file_path": "examples/flask_demo/services/user_service",
        "lineno": 10,
        "end_lineno": 28,
        "execution_time": 100,
        "count": 5,
        "label": 0
    }
] # 元素名称列表


class ClusteringEnv(Env):
    def __init__(self, sim_matrix, max_k=10):
        self.sim_matrix = sim_matrix
        self.max_k = max_k
        self.current_k = 2
        self.action_space = spaces.Discrete(3)  # 动作：-1, 0, +1（调整k）
        self.observation_space = spaces.Box(low=0, high=1, shape=(1,))

    def step(self, action):
        self.current_k += action - 1  # 动作0: k-1, 1: 保持, 2: k+1
        self.current_k = np.clip(self.current_k, 2, self.max_k)

        # 执行聚类（此处简化为直接使用相似度矩阵）
        # 实际中可替换为层次聚类或谱聚类
        from sklearn.cluster import SpectralClustering
        clusters = SpectralClustering(n_clusters=self.current_k, affinity='precomputed').fit_predict(self.sim_matrix)

        # 计算奖励（轮廓系数）
        if len(np.unique(clusters)) >= 2:
            reward = silhouette_score(self.sim_matrix, clusters, metric='precomputed')
        else:
            reward = -1  # 惩罚无效聚类
        return np.array([self.current_k / self.max_k]), reward, False, {}

    def reset(self):
        self.current_k = 2
        return np.array([self.current_k / self.max_k])


# 训练RL智能体
env = ClusteringEnv(sim_matrix)
model = PPO("MlpPolicy", env, verbose=1)
model.learn(total_timesteps=1000)

# 使用训练好的策略聚类
obs = env.reset()
action, _ = model.predict(obs)
final_k = env.current_k + action - 1
final_clusters = SpectralClustering(n_clusters=final_k, affinity='precomputed').fit_predict(sim_matrix)