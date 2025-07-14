import shutil
import os

# source = "D:\\Programs\\MicroService\\micro\\demo"
# target = "D:\\Programs\\MicroService\\call-graph\\tools\\demo"


source = "D:/Programs/MicroService/micro/demos/demo1-origin"

source = "D:/Programs/MicroService/traveldog/traveldog"
source = "D:/Projects/MicroService/traveldog/microservice"
target = source
source = source + "-backup"

# 如果目标文件夹存在，则先删除
if os.path.exists(target):
    shutil.rmtree(target)
    print(f"目标文件夹 '{target}' 已被删除。")

shutil.copytree(source, target, dirs_exist_ok=True)
print(f"文件夹 '{source}' 已成功复制为 '{target}'。")