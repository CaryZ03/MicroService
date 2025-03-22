import os
from django.core.wsgi import get_wsgi_application
from wsgiref.simple_server import make_server
import sys


def main(args):
    sys.path.insert(0, 'django_task/django_job')
    # 设置 Django 环境变量
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'django_job.settings')
    # 获取 WSGI 应用
    application = get_wsgi_application()
    # 创建服务器
    httpd = make_server('localhost', 8000, application)
    print("Starting development server at http://localhost:8000/")
    httpd.serve_forever()


if __name__ == '__main__':
    main(None)
