# MicroService
微服务拆分工具



micro文件夹下是Python端，

- Project是强化学习
- utils是工具类
- entry.py是入口函数，详细的调用在这里



call-graph文件夹下是Java工具

- tools是静态分析、拆分什么的，进文件夹直接maven或者IDEA跑就行
  - 其中CallGraphGenerator是静态分析和打桩
  - SpringCloudReconstructor是代码重构



把tools跑起来以后跑micro，micro调用tools

micro里面最上面定义了src目录，指向目标代码文件夹，main函数里调用了多个模块，可以分开使用

注释什么的没怎么写，但整体应该还比较好读，有啥问题再解决
