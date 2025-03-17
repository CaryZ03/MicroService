import os
import find_imports
import shutil


def main(repo_dir, config_path):
    config = find_imports.Config(config_path)
    # shutil.rmtree(config.config['imports_output_dir'], ignore_errors=True)
    for file in os.listdir(repo_dir):
        if file.endswith('.py'):
            find_imports.main(os.path.join(repo_dir, file), config_path)
        if os.path.isdir(os.path.join(repo_dir, file)):
            main(os.path.join(repo_dir, file), config_path)


if __name__ == '__main__':
    main('job', 'config/config.json')
