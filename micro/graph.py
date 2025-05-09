import json

import networkx as nx


def generate_echarts_html(graph: nx.DiGraph, output_file: str = "dag.html"):
    # 生成节点数据
    nodes = [{
        'id': node,
        'name': node.replace("com.example.demo.", ""),
        'symbolSize': min(graph.nodes[node]['count'] / 5 + 30, 100),  # 限制最大尺寸
        'value': graph.nodes[node],
        'itemStyle': {
            'color': '#91cc75' if graph.nodes[node]['type'] == 'entry' else  # 端口绿色
            ('#ff7875' if graph.nodes[node]['type'] == 'database' else '#5470c6'),  # 数据库红色/服务蓝色
            'shadowBlur': 10,
            'shadowColor': 'rgba(0, 0, 0, 0.3)'
        },
        'label': {
            'show': True  # 默认显示标签
        }
    } for node in graph.nodes]

    # 生成边数据
    links = [{
        'source': src,
        'target': dst,
        'value': graph[src][dst]['weight'],
        'lineStyle': {
            'width': graph[src][dst]['weight'] / 2 + 1,
            'color': {
                'type': 'linear',
                'x': 0,
                'y': 0,
                'x2': 1,
                'y2': 0,
                'colorStops': [{
                    'offset': 0, 'color': '#91d5ff'  # 渐变色边
                }, {
                    'offset': 1, 'color': '#096dd9'
                }]
            }
        }
    } for src, dst in graph.edges]

    # ECharts template
    template = f"""
    <!DOCTYPE html>
    <html style="height:100%">
    <head>
        <meta charset="utf-8">
        <title>服务依赖关系图</title>
        <script src="https://cdn.staticfile.org/echarts/5.4.2/echarts.min.js"></script>
        <style>
            body, html {{ margin:0; padding:0; height:100%; width:100%; }}
            #main {{ height:100vh; width:100vw; }}
        </style>
    </head>
    <body>
        <div id="main"></div>
        <script>
            var chart = echarts.init(document.getElementById('main'));

            var option = {{
                title: {{
                    text: '服务依赖关系图',
                    left: 'center',
                    textStyle: {{ color: '#666', fontSize: 24 }}
                }},
                tooltip: {{
                    trigger: 'item',
                    formatter: function(params) {{
                        if(params.dataType === 'node') {{
                            return `服务：${{params.name}}<br>
                                    调用次数：${{params.data.value.count}}<br>
                                    总耗时：${{params.data.value.execution_time/1000}}ms`;
                        }}
                        return `权重：${{params.value}}`;
                    }}
                }},
                animationDuration: 2000,
                series: [{{
                    type: 'graph',
                    layout: 'force',
                    force: {{
                        repulsion: 3000,
                        gravity: 0.1,
                        edgeLength: 150,
                        layoutAnimation: true
                    }},
                    roam: true,
                    focusNodeAdjacency: true,
                    edgeSymbol: ['none', 'arrow'],
                    edgeSymbolSize: [0, 15],
                    label: {{
                        show: true,
                        position: 'right',
                        fontSize: 12,
                        color: '#000',
                        formatter: function(params) {{
                            // 名称截断显示
                            return params.name.length > 15 ? 
                                params.name.substr(0,12)+'...' : 
                                params.name;
                        }},
                        emphasis: {{ show: false }}  // 悬停时不显示标签
                    }},
                    emphasis: {{
                        label: {{ show: false }},
                        itemStyle: {{
                            shadowBlur: 20,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }}
                    }},
                    lineStyle: {{
                        curveness: 0.2,
                        opacity: 0.8
                    }},
                    data: {json.dumps(nodes, indent=4)},
                    links: {json.dumps(links, indent=4)},
                    categories: [{{
                        name: '数据库节点',
                        itemStyle: {{ color: '#ff7875' }}
                    }}, {{
                        name: '端口节点',
                        itemStyle: {{ color: '#91cc75' }}
                    }}, {{
                        name: '服务节点',
                        itemStyle: {{ color: '#5470c6' }}
                    }}]
                }}],
                legend: {{
                    orient: 'vertical',
                    right: 20,
                    top: 60,
                    textStyle: {{ color: '#666' }}
                }}
            }};

            chart.setOption(option);

            // 窗口自适应
            window.addEventListener('resize', () => chart.resize());

            // 双击重置视图
            chart.getZr().on('dblclick', () => {{
                chart.setOption(option);
            }});
        </script>
    </body>
    </html>
    """

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(template)
