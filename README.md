# AutoTracer

#### 介绍
本仓库的功能是实现一个能够对python web应用代码进行动态和静态追踪的小工具，在分析完成之后，会生成一个调用图，里面包含了每个节点的所在位置、与其它节点的层级情况、不同节点之间的调用关系以及IO和数据库操作统计

#### 使用说明

1. 按照例子编写`config/config.json`
2. 将flask项目放在job文件夹中
3. 运行`analyze_repository.py`，生成静态分析结果
4. 运行`monkey_patch_trace.py`，生成动态分析结果
5. 运行`get_data.py`，将两者的结果融合后输出到文件夹中

