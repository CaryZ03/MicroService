import torch
print(torch.__version__)  # 看看版本号
print(torch.cuda.is_available())  # 检查是否支持 CUDA
print(torch.cuda.device_count())  # 显示可用的 GPU 数量
print(torch.cuda.get_device_name(0))  # 显示第一个 GPU 的名称（如果存在）
