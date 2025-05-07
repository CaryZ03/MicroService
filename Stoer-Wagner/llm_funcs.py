import requests


def format_function_name(formal_func_name, url, token, model_name):
    """
    从硅基流动网站调用api，并且将函数名转换成驼峰命名
    :param formal_func_name: 转换前的函数名
    :param token: api的token
    :return: 以驼峰命名形式返回的函数名
    """

    payload = {
        "model": model_name,
        "messages": [
            {
                "role": "user",
                "content": f"我现在有一个函数名，可能包含中文拼音或英文缩写。"
                           f"我希望你能给我返回一个修改后的函数名，将它们转换为完整的英文单词，并满足驼峰命名原则。"
                           f"下面是函数名 "
                           f"{formal_func_name}. 注意你只需要给我返回修改后的函数名，不需要返回其它的任何内容， "
                           f"如果你认为这个函数名没有意义，你可以直接返回None"
            }
        ],
        "stream": False,
        "max_tokens": 512,
        "stop": None,
        "temperature": 0.7,
        "top_p": 0.7,
        "top_k": 50,
        "frequency_penalty": 0.5,
        "n": 1,
        "response_format": {"type": "text"},
    }
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    response = requests.request("POST", url, json=payload, headers=headers)

    dict_data = eval(response.text)
    return dict_data['choices'][0]['message']['content']


def judge_semantic_similarity(func_name1, func_name2, url, token, model_name):
    """
    判断两个函数名的语义相似度
    :param func_name1: 已经经过了预处理的函数名1
    :param func_name2: 已经经过了预处理的函数名2
    :param token: api的token
    :return: 相似度（为一个在0-1之间的浮点数）
    """

    payload = {
        "model": model_name,
        "messages": [
            {
                "role": "user",
                "content": f"I have two function names that may contain Chinese pinyin or English abbreviations. I "
                           f"hope you can return me a similarity score between 0 and 1 to indicate the semantic "
                           f"similarity between the two function names. The first function name is: {func_name1}. The "
                           f"second function name is: {func_name2}. Note that you only need to return the similarity "
                           f"score to me, without any other content"
            }
        ],
        "stream": False,
        "max_tokens": 512,
        "stop": None,
        "temperature": 0.7,
        "top_p": 0.7,
        "top_k": 50,
        "frequency_penalty": 0.5,
        "n": 1,
        "response_format": {"type": "text"},
    }
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    response = requests.request("POST", url, json=payload, headers=headers)

    dict_data = eval(response.text)
    print(func_name1, func_name2, dict_data['choices'][0]['message']['content'])
    return float(dict_data['choices'][0]['message']['content'])