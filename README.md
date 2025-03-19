# AutoTracer

#### 介绍
本仓库的功能是实现一个能够对python web应用代码进行动态和静态追踪的小工具，在分析完成之后，会生成一个调用图，里面包含了每个节点的所在位置、与其它节点的层级情况、不同节点之间的调用关系以及IO和数据库操作统计

#### 当前例子的使用说明

1. 在当前的项目文件夹下创建`.venv`作为虚拟环境，依据`requirements.txt`中内容安装依赖
2. 按照使用的电脑的路径修改root_dir、venv_dir、task_dir
3. 运行`analyze_repository.py`进行静态分析
4. 运行`monkey_patch_trace.py`启动动态分析追踪
5. 运行`call_flask.py`调用api
6. 执行`dependence.py`获取节点之间的方法依赖度和语义依赖度
7. 执行`get_k_means.py`进行聚类的划分

#### 先决条件：

由于windows的路径分隔符问题，该项目目前只支持在linux和macos下运行

