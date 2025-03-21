import os
import find_imports
from config_class import Config
import shutil


def remove_dir(config_path):
    config = Config(config_path)
    shutil.rmtree(config.config['imports_output_dir'], ignore_errors=True)
    os.makedirs(config.config['imports_output_dir'], exist_ok=True)


def main(repo_dir, config_path):
    for file in os.listdir(repo_dir):
        if file.endswith('.py'):
            find_imports.main(os.path.join(repo_dir, file), config_path)
        if os.path.isdir(os.path.join(repo_dir, file)):
            main(os.path.join(repo_dir, file), config_path)


if __name__ == '__main__':
    CONFIG_PATH = 'config/config.json'
    remove_dir(CONFIG_PATH)
    main('job', CONFIG_PATH)
