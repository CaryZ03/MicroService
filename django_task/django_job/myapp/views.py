from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse

from myapp.models import MyUser


def calculate_sum(a, b):
    """
    计算两个数的和
    """
    return a + b


def index(request):
    # 正确创建 MyUser 对象
    result = calculate_sum(3, 5)
    user = MyUser(name='John', age=30)
    # 保存对象
    user.save()
    return HttpResponse(f"The sum is: {result}")
