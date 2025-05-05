import shutil

# source = "D:\\Programs\\MicroService\\micro\\demo"
# target = "D:\\Programs\\MicroService\\call-graph\\tools\\demo"


source = "D:/Programs/MicroService/micro/demos/demo1-origin-copy"
target = "D:/Programs/MicroService/micro/demos/demo1-origin"

shutil.copytree(source, target, dirs_exist_ok=True)
# print(f"文件夹 '{source}' 已成功复制为 '{target}'。")