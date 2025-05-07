import itertools
import json
import re
from typing import List, Any

import numpy as np
import spacy
from tqdm import tqdm

import llm_funcs
from parse_config import Config


class DependenceGetter:
    def __init__(self, config_path: str):
        self.config = Config(config_path).config
        self.graph_pairs_data = self.load_json_data(self.config['graph_pairs_json_path'])
        self.all_service: List[str] = self.graph_pairs_data['all_services']
        self.token = self.config['api']['api_token']
        self.url = self.config['api']['api_url']
        self.model_name = self.config['api']['model_name']
        self.semantic_similarity_numpy: np.ndarray = np.zeros((len(self.all_service), len(self.all_service)))
        self.nlp = spacy.load("en_core_web_md")
        self.keywords: List[str] = []

    @staticmethod
    def load_json_data(json_path: str) -> Any:
        with open(json_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        return data

    @staticmethod
    def extract_nouns(text, nlp):
        """
        识别出句子中所有的名词，会进行无关词的删除
        :param text: 输入的文本
        :param nlp: spaCy NLP 对象
        :return: 所有的名词
        """
        # 加载英语模型

        doc = nlp(text)

        # 提取句子中的所有名词以及宾语
        nouns = []

        saved_words = ['property', 'util', 'manager', 'result', 'advice', 'response', 'filter', 'config', 'interceptor',
                       'handler', 'validator', 'aspect', 'configuration', 'exception', 'view', 'listener', 'error',
                       'model',
                       'controller', 'mapper', 'service', 'setting', 'data', 'constant', 'route', 'entity',
                       'repository']

        if len(doc) == 1:
            return text  # 假设单个词直接作为名词返回

        # 遍历词汇并提取名词
        for token in doc:
            # 如果是名词，或者是宾语，也作为名词处理
            if token.pos_ == "NOUN" or token.pos_ == "PROPN" or token.dep_ in ("dobj", "iobj"):
                # 还原词形并去除无效词
                if token.lemma_.lower() not in saved_words:
                    nouns.append(token.lemma_.lower())

        return " ".join(nouns)

    @staticmethod
    def split_function_name(func_name):
        """
        拆分函数名（支持下划线命名和驼峰命名）
        """
        # 处理下划线命名
        if '_' in func_name:
            words = func_name.split('_')
        else:
            # 处理驼峰命名
            words = [word.lower() for word in
                     re.sub('([A-Z][a-z]+)', r' \1', re.sub('([A-Z]+)', r' \1', func_name)).split()]
        return ' '.join(words)

    def get_service_keywords(self, endpoint_name: str) -> str:
        splits = endpoint_name.removeprefix(self.config['root_dir']).split('.')
        if len(splits) < 2:
            return endpoint_name
        func_name = splits[-2]
        file_name = splits[:-2]
        format_func_name = llm_funcs.format_function_name(func_name, self.url, self.token, self.model_name)
        if format_func_name == 'None':
            format_func_name = func_name
        nouns = self.extract_nouns(self.split_function_name(format_func_name), self.nlp)
        if nouns == '':
            for i in range(len(file_name) - 1, -1, -1):
                nouns_tmp = self.extract_nouns(file_name[i], self.nlp)
                if nouns_tmp != '':
                    nouns = nouns_tmp
                    break
        return nouns

    def pre_tackle_service_name(self):
        for service in self.all_service:
            self.keywords.append(self.get_service_keywords(service))

    def calculate_similarity(self):
        # 计算总对数：C(n,2) = n*(n-1)/2
        total_pairs = len(self.keywords) * (len(self.keywords) - 1) // 2

        # 生成所有需要遍历的 (i,j) 组合（避免重复）
        pairs = itertools.combinations(range(len(self.keywords)), 2)

        # 使用 tqdm 包装组合迭代
        for i, j in tqdm(pairs, total=total_pairs, desc="语义相似度计算"):
            if self.keywords[i] == '' or self.keywords[j] == '':
                continue
            similarity = llm_funcs.judge_semantic_similarity(
                self.keywords[i], self.keywords[j], self.url, self.token, self.model_name
            )
            self.semantic_similarity_numpy[i][j] = similarity
            self.semantic_similarity_numpy[j][i] = similarity

        # 保存矩阵
        np.save(self.config['semantic_similarity_numpy_path'], self.semantic_similarity_numpy)

    def get_dependence(self):
        self.pre_tackle_service_name()
        self.calculate_similarity()


def main():
    config_path = './config.json'
    dependence_getter = DependenceGetter(config_path)
    dependence_getter.get_dependence()


if __name__ == '__main__':
    main()
