import ast
import json
import keyword
import os
from config_class import Config


def ast_to_dict(node):
    if isinstance(node, ast.AST):
        return {key: ast_to_dict(value) for key, value in vars(node).items()}
    elif isinstance(node, list):
        return [ast_to_dict(item) for item in node]
    else:
        return node


class FuncRelationShip:
    def __init__(self, node=None, fileName=None):
        self.node = node
        self.func_name = node.name if node is not None and hasattr(node, 'name') else 'root'
        self.imports = []
        self.children = []
        self.father = None
        self.use_variables = set()
        self.def_variables = set()
        self.global_variables = set()
        self.node_type = 'function' if node is not None and isinstance(node,
                                                                       ast.FunctionDef) else 'class' if isinstance(node,
                                                                                                                   ast.ClassDef) else 'root'
        self.lineno = self.getstart_lineno(node) if node is not None else None
        self.end_lineno = node.end_lineno if node is not None and hasattr(node, 'end_lineno') else None
        self.fileName = fileName
        self.return_variables = set()

    def getstart_lineno(self, node):
        if not hasattr(node, 'lineno'):
            return None
        start_lineno = node.lineno
        if hasattr(node, 'decorator_list'):
            for decorator in node.decorator_list:
                start_lineno = min(start_lineno, decorator.lineno)
        return start_lineno

    def to_dict(self):
        return {
            'func_name': self.func_name,
            'imports': [self.import_to_dict(imp) for imp in self.imports],
            'children': [child.to_dict() for child in self.children],
            'use_variables': list(self.use_variables),
            'def_variables': list(self.def_variables),
            'global_variables': list(self.global_variables),
            'node_type': self.node_type,
            'node': ast.dump(self.node) if self.node else None,
            'lineno': self.lineno,
            'end_lineno': self.end_lineno,
            'fileName': self.fileName,
            'return_variables': list(self.return_variables)
        }

    @staticmethod
    def import_to_dict(imp):
        if isinstance(imp, tuple):
            return {'name': imp[0], 'node': ast.dump(imp[1])}
        return imp

    @staticmethod
    def from_dict(data, fileName):
        node = ast.parse(data['node']) if data['node'] else None
        func_node = FuncRelationShip(node, fileName)
        func_node.fileName = fileName
        func_node.func_name = data['func_name']
        func_node.imports = [(imp['name'], ast.parse(imp['node'])) for imp in data['imports']]
        func_node.use_variables = set(data['use_variables'])
        func_node.def_variables = set(data['def_variables'])
        func_node.global_variables = set(data['global_variables'])
        func_node.node_type = data['node_type']
        func_node.lineno = data['lineno']
        func_node.end_lineno = data['end_lineno']
        func_node.return_variables = set(data['return_variables'])
        func_node.children = [FuncRelationShip.from_dict(child, fileName) for child in data['children']]
        for child in func_node.children:
            child.father = func_node
        return func_node


class CodeAnalyzer(ast.NodeVisitor):
    def __init__(self, file_path=None):
        self.file_path = file_path
        self.imports = []
        self.functions = []
        self.classes = []
        self.root_node = None
        self.current_node = None

    def find_module_name(self, module_name, current_node_imports, imports):
        if len(current_node_imports) != 0:
            current_node_imports_reverse = list(reversed(current_node_imports))
            for import_name, import_node in current_node_imports_reverse:
                if module_name == import_name:
                    return import_name, import_node
        if len(imports) != 0:
            imports_reverse = list(reversed(imports))
            for import_name, import_node in imports_reverse:
                if module_name == import_name:
                    return import_name, import_node
        return None, None

    def add_init_node(self, current_node):
        if current_node is None:
            return
        for node in current_node.children:
            self.add_init_node(node)
        if current_node.node_type == 'class':
            flag = False
            for child in current_node.children:
                if child.func_name == '__init__':
                    flag = True
            if not flag:
                node = FuncRelationShip(ast.FunctionDef(name='__init__', args=ast.arguments(args=[], vararg=None,
                                                                                           kwonlyargs=[], kw_defaults=[],
                                                                                           kwarg=None, defaults=[]),
                                                        body=[], decorator_list=[]), self.file_path)
                node.lineno = current_node.lineno
                node.end_lineno = current_node.lineno
                node.father = current_node
                current_node.children.append(node)

    def find_stmt_imports(self, stmt, imports, current_node_imports, results, funcNode):
        if isinstance(stmt, ast.ClassDef):
            child_node = FuncRelationShip(stmt, self.file_path)
            funcNode.children.append(child_node)
            child_node.father = funcNode
            tmp_imports = imports.copy()
            tmp_imports.extend(current_node_imports)
            self.find_class_imports(child_node, tmp_imports)
        elif isinstance(stmt, ast.FunctionDef):
            child_node = FuncRelationShip(stmt, self.file_path)
            funcNode.children.append(child_node)
            child_node.father = funcNode
            tmp_imports = imports.copy()
            tmp_imports.extend(current_node_imports)
            self.find_function_imports(child_node, tmp_imports)
        elif isinstance(stmt, (ast.Import, ast.ImportFrom)):
            for alias in stmt.names:
                current_node_imports.append((alias.name if alias.asname is None else alias.asname, stmt))
        elif isinstance(stmt, ast.Global):
            for name in stmt.names:
                funcNode.global_variables.add(name)
                funcNode.use_variables.add(name)
        elif isinstance(stmt, ast.Call):
            func = stmt.func
            while hasattr(func, 'value'):
                func = func.value
                while hasattr(func, 'func'):
                    func = func.func
            module_name = func.id
            import_name, import_node = self.find_module_name(module_name, current_node_imports, imports)
            if import_name is not None and import_node is not None:
                results.append((import_name, import_node))
            for arg in stmt.args:
                self.find_stmt_imports(arg, imports, current_node_imports, results, funcNode)
        elif isinstance(stmt, ast.Name):
            if isinstance(stmt.ctx, (ast.Store, ast.Load)):
                if isinstance(stmt.ctx,
                              ast.Store) and stmt.id not in funcNode.global_variables and not keyword.iskeyword(stmt.id):
                    funcNode.def_variables.add(stmt.id)
                elif stmt.id not in funcNode.def_variables and stmt.id not in self.functions and not keyword.iskeyword(
                        stmt.id):
                    funcNode.use_variables.add(stmt.id)
            import_name, import_node = self.find_module_name(stmt.id, current_node_imports, imports)
            if import_name is not None and import_node is not None:
                results.append((import_name, import_node))
        elif isinstance(stmt, ast.Return):
            self.find_stmt_imports(stmt.value, imports, current_node_imports, results, funcNode)
            funcNode.return_variables.add(ast.unparse(stmt.value).strip())
        elif isinstance(stmt, ast.AST):
            for _, value in vars(stmt).items():
                self.find_stmt_imports(value, imports, current_node_imports, results, funcNode)
        elif isinstance(stmt, list):
            for item in stmt:
                self.find_stmt_imports(item, imports, current_node_imports, results, funcNode)

    def find_class_imports(self, class_node, imports):
        current_node = class_node.node
        current_node_imports = []
        for decorator in current_node.decorator_list:
            self.find_stmt_imports(decorator, imports, current_node_imports, class_node.imports, class_node)
        for stmt in current_node.body:
            self.find_stmt_imports(stmt, imports, current_node_imports, class_node.imports, class_node)

    def find_function_imports(self, funcNode, imports):
        current_node_imports = []
        current_node = funcNode.node
        for decorator in current_node.decorator_list:
            self.find_stmt_imports(decorator, imports, current_node_imports, funcNode.imports,
                                   funcNode)
        for arg in current_node.args.args:
            funcNode.def_variables.add(arg.arg)
        for stmt in current_node.body:
            self.find_stmt_imports(stmt, imports, current_node_imports, funcNode.imports,
                                   funcNode)

    def analyze(self):
        with open(self.file_path, 'r', encoding='utf-8') as file:
            code = file.read()
        tree = ast.parse(code)
        self.root_node = FuncRelationShip(tree, self.file_path)
        self.current_node = self.root_node
        self.visit(tree)

    def visit_Import(self, node):
        for alias in node.names:
            self.imports.append((alias.name if alias.asname is None else alias.asname, node))
        self.generic_visit(node)

    def visit_ImportFrom(self, node):
        for alias in node.names:
            self.imports.append((alias.name if alias.asname is None else alias.asname, node))
        self.generic_visit(node)

    def visit_FunctionDef(self, node):
        funcNode = FuncRelationShip(node, self.file_path)
        self.functions.append(funcNode.func_name)
        tmp_imports = self.imports.copy()
        self.current_node.children.append(funcNode)
        funcNode.father = self.current_node
        self.current_node = funcNode
        self.find_function_imports(funcNode, tmp_imports)
        self.current_node = funcNode.father

    def visit_ClassDef(self, node):
        class_node = FuncRelationShip(node, self.file_path)
        self.functions.append(class_node.func_name)
        class_node.father = self.current_node
        self.current_node.children.append(class_node)
        self.current_node = class_node
        tmp_imports = []
        self.find_class_imports(class_node, tmp_imports)
        self.current_node = class_node.father

    def print_node(self, node):
        print(node.to_dict())

    def print_tree(self, node):
        self.print_node(node)
        for child in node.children:
            self.print_tree(child)

    def save_to_json(self, file_path):
        with open(file_path, 'w') as file:
            json.dump(self.root_node.to_dict(), file, indent=4)

    def load_from_json(self, file_path):
        with open(file_path, 'r') as file:
            data = json.load(file)
        self.root_node = FuncRelationShip.from_dict(data, self.file_path)


def main(input_file_path, config_path):
    config = Config(config_path)
    analyzer = CodeAnalyzer(input_file_path)
    analyzer.analyze()
    output_file_path = os.path.join(config.config['imports_output_dir'],
                                    input_file_path.replace('/', '&').replace('py', 'json'))
    analyzer.add_init_node(analyzer.root_node)
    analyzer.save_to_json(output_file_path)


if __name__ == '__main__':
    main('formal_job/app/routes.py', 'config/config.json')
