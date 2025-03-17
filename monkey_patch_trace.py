"""
这个项目是实现了一个通用的代码追踪器，可以用于追踪 Python 代码的执行过程，包括函数调用、I/O 操作和数据库操作。
"""
import sys
import threading
import time
import json
from collections import defaultdict
import shutil
import os
import re
import importlib
import ast
from sqlalchemy import Engine


class Config:
    """
    用于引入配置文件，配置文件中包含了任务类型、任务文件、任务配置等信息。
    """

    def __init__(self, config_path):
        self.config_path = config_path
        self.config = None
        self.load_config()
        self.task_type = self.config['task_type']

    def load_config(self):
        """
        从配置文件中加载配置信息。
        :return: 无
        """
        with open(self.config_path, encoding='utf-8') as file:
            self.config = json.load(file)


class UniversalTracer:
    """
    一个通用的追踪器，可以针对flask和django等框架进行追踪。
    """

    def __init__(self, config):
        self.thread_local = threading.local()
        self.trace_data = defaultdict(list)
        self.io_data = defaultdict(list)  # 用于存储普通 I/O 操作的追踪数据
        self.db_data = defaultdict(list)  # 用于存储数据库操作的追踪数据
        self.enabled = False
        self.has_saved = threading.local()
        self.save_count = 0
        self.config = config

    def start_tracing(self):
        """
        开始追踪函数调用。
        :return: 无
        """
        if not self.enabled:
            sys.setprofile(self.trace_calls)
            self.enabled = True

    def stop_tracing(self):
        """
        停止追踪函数调用。
        :return: 无
        """
        if self.enabled:
            sys.setprofile(None)
            self.enabled = False

    def extract_table_name(self, sql):
        """
        从 SQL 语句中提取表名。
        """
        # 匹配 FROM 子句中的表名（支持双引号或单引号）
        match = re.search(r'FROM\s+["\']?(\w+)["\']?', sql, re.IGNORECASE)
        if match:
            return match.group(1)
        # 匹配 INSERT INTO 子句中的表名（支持双引号或单引号）
        match = re.search(r'INSERT\s+INTO\s+["\']?(\w+)["\']?', sql, re.IGNORECASE)
        if match:
            return match.group(1)
        # 匹配 UPDATE 子句中的表名（支持双引号或单引号）
        match = re.search(r'UPDATE\s+["\']?(\w+)["\']?', sql, re.IGNORECASE)
        if match:
            return match.group(1)
        # 匹配 DELETE FROM 子句中的表名（支持双引号或单引号）
        match = re.search(r'DELETE\s+FROM\s+["\']?(\w+)["\']?', sql, re.IGNORECASE)
        if match:
            return match.group(1)
        return None

    def trace_calls(self, frame, event, arg):
        """
        用于追踪函数调用的回调函数。
        :param frame: 追踪的帧
        :param event: 事件
        :param arg: 参数
        :return: 无
        """
        if event == 'call':
            if not hasattr(self.thread_local, 'call_stack'):
                self.thread_local.call_stack = []
                self.thread_local.start_time = time.time()

            caller_frame = frame.f_back
            caller_first_lineno = None
            if caller_frame:
                caller_function_name = caller_frame.f_code.co_name
                caller_file = caller_frame.f_code.co_filename
                caller_lineno = caller_frame.f_lineno
                caller_first_lineno = caller_frame.f_code.co_firstlineno
            else:
                caller_function_name = None
                caller_file = None
                caller_lineno = None

            called_function_name = frame.f_code.co_name
            called_file = frame.f_code.co_filename
            called_lineno = frame.f_lineno

            if called_function_name in {'__init__', '__str__', '__repr__'}:
                cls = frame.f_locals.get('self', None).__class__
                module_name = cls.__module__
                module = importlib.import_module(module_name)
                called_file = module.__file__
                class_name = cls.__name__

                if called_lineno == 1:
                    with open(called_file, 'r', encoding='utf-8') as file:
                        code = file.read()
                        tree = ast.parse(code)
                        for node in ast.walk(tree):
                            if isinstance(node, ast.ClassDef) and node.name == class_name:
                                called_lineno = node.lineno
                                break

            call_info = {
                'caller_function_name': caller_function_name,
                'caller_file': caller_file,
                'caller_lineno': caller_lineno,
                'called_function_name': called_function_name,
                'called_file': called_file,
                'called_lineno': called_lineno,
                'start_time': time.time(),
                'caller_first_lineno': caller_first_lineno,
                'io_nodes': [],
                'db_nodes': []
            }

            self.thread_local.call_stack.append(call_info)

        elif event == 'c_call':
            called_function_name = arg.__name__
            called_file = frame.f_code.co_filename
            called_lineno = frame.f_lineno
            if not hasattr(self.thread_local, 'call_stack'):
                self.thread_local.call_stack = []
                self.thread_local.start_time = time.time()

            caller_frame = frame.f_back
            caller_first_lineno = None
            if caller_frame:
                caller_function_name = self.thread_local.call_stack[-1][
                    'called_function_name'] if self.thread_local.call_stack else "None"
                caller_file = caller_frame.f_code.co_filename
                caller_lineno = self.thread_local.call_stack[-1][
                    'called_lineno'] if self.thread_local.call_stack else "None"
                caller_first_lineno = caller_frame.f_code.co_firstlineno
            else:
                caller_function_name = None
                caller_file = None
                caller_lineno = None

            call_info = {
                'caller_function_name': caller_function_name,
                'caller_file': caller_file,
                'caller_lineno': caller_lineno,
                'called_function_name': called_function_name,
                'called_file': called_file,
                'called_lineno': called_lineno,
                'start_time': time.time(),
                'caller_first_lineno': caller_first_lineno,
                'io_nodes': [],
                'db_nodes': []
            }

            self.thread_local.call_stack.append(call_info)

        if event in {'return', 'c_return'}:

            if hasattr(self.thread_local, 'call_stack') and self.thread_local.call_stack:
                call_info = self.thread_local.call_stack.pop()

                exec_time = time.time() - call_info['start_time']

                call_info['exec_time'] = exec_time

                self.trace_data[threading.current_thread().ident].append(call_info)

        return self.trace_calls

    def save_to_json(self, filename=None):
        """
        将追踪数据保存到 JSON 文件中。
        :param filename: 保存的文件名
        :return: 无
        """
        save_dir = self.config.config['trace_output_dir']
        if filename is None:
            filename = f"{save_dir}/{self.save_count}_trace_output.json"
        os.makedirs(save_dir, exist_ok=True)

        while len(self.thread_local.call_stack) > 0:
            call_info = self.thread_local.call_stack.pop()
            call_info['exec_time'] = 0
            self.trace_data[threading.current_thread().ident].append(call_info)

        result = {
            'trace_data': self.trace_data,
            'io_data': self.io_data,
            'db_data': self.db_data
        }

        with open(filename, 'w', encoding='utf-8') as file:
            json.dump(result, file, indent=4)
        print(f"Trace data saved to {filename}.")
        self.save_count += 1

    def run(self, config):
        """
        运行追踪器。
        :param config:
        :return: 无
        """
        import argparse

        if config.task_type == 'django':
            patch_django(self)
        elif config.task_type == 'flask':
            patch_flask(self)
            setup_sqlalchemy_events(codeTracer)

        task_config = {}
        task_args = {}
        if 'task_config' in config.config:
            task_config = config.config['task_config']
        if 'default_args' in task_config:
            task_args.update(task_config['default_args'])
        parser = argparse.ArgumentParser()
        args = parser.parse_args([])
        for key, value in task_args.items():
            setattr(args, key.replace('-', '_'), value)

        if 'task_file' in config.config:
            module_name = os.path.splitext(config.config['task_file'])[0].replace('/', '.')
            module = importlib.import_module(module_name)
            module.main(args)


def patch_flask(tracer):
    """
    用于追踪 Flask 的请求处理过程。
    :param tracer:
    :return: 无返回值
    """
    from flask import Flask

    original_dispatch_request = Flask.dispatch_request

    def dispatch_request(self):
        tracer.start_tracing()
        try:
            result = original_dispatch_request(self)
        finally:
            tracer.stop_tracing()
            tracer.save_to_json()
        return result

    Flask.dispatch_request = dispatch_request


def patch_django(tracer):
    """
    用于追踪 Django 的请求处理过程。
    :param tracer:
    :return: 无返回值
    """
    from django.core.handlers.wsgi import WSGIHandler
    original_call = WSGIHandler.__call__

    def traced_call(self, environ, start_response):
        tracer.start_tracing()
        try:
            result = original_call(self, environ, start_response)
        finally:
            tracer.stop_tracing()
            tracer.save_to_json()
        return result

    WSGIHandler.__call__ = traced_call


def setup_sqlalchemy_events(tracer):
    """
    用于追踪 SQLAlchemy 的数据库操作。
    :param tracer:
    :return: 无返回值
    """
    from sqlalchemy import event

    @event.listens_for(Engine, "before_execute")
    def before_execute(conn, clauseelement, multiparams, params):
        sql = str(clauseelement)
        table_name = tracer.extract_table_name(sql)
        if table_name:
            print(f"Executing SQL on table: {table_name}")
            db_info = {
                'db_function': 'execute',
                'table': table_name,
                'sql': sql,
                'start_time': time.time(),
                'exec_time': None
            }
            call_stack = getattr(tracer.thread_local, "call_stack", None)
            if call_stack and len(call_stack) > 0:
                for item in list(reversed(call_stack)):
                    if (tracer.config.config['task_dir'] in item['called_file']
                            and tracer.config.config['venv_dir'] not in item['called_file']):
                        item.setdefault("db_nodes", []).append(db_info)
                        print(item)
                        break
            else:
                tracer.db_data[threading.current_thread().ident].append(db_info)

    @event.listens_for(Engine, "after_execute")
    def after_execute(conn, clauseelement, multiparams, params, result):
        end_time = time.time()
        sql_str = str(clauseelement)
        call_stack = getattr(tracer.thread_local, "call_stack", None)
        updated = False
        if call_stack and len(call_stack) > 0:
            for item in list(reversed(call_stack)):
                if (tracer.config.config['task_dir'] in item['called_file'] and
                        tracer.config.config['venv_dir'] not in item['called_file']):
                    for db_info in item.get("db_nodes", []):
                        if db_info["sql"] == sql_str and db_info["exec_time"] is None:
                            db_info["exec_time"] = end_time - db_info["start_time"]
                            updated = True
                            break
        if not updated:
            for db_info in tracer.db_data[threading.current_thread().ident]:
                if db_info["sql"] == sql_str and db_info["exec_time"] is None:
                    db_info["exec_time"] = end_time - db_info["start_time"]
                    break


if __name__ == '__main__':
    CONFIG_FILE_PATH = 'config/config.json'
    config_data = Config(CONFIG_FILE_PATH)
    shutil.rmtree(config_data.config['trace_output_dir'], ignore_errors=True)
    codeTracer = UniversalTracer(config_data)
    codeTracer.run(config_data)
