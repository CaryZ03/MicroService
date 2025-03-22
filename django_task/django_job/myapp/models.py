from django.db import models


class MyUser(models.Model):
    """
    用户模型
    """
    name = models.CharField(max_length=100)
    age = models.IntegerField()

    def __init__(self, *args, **kwargs):
        # 调用父类的 __init__ 方法
        super().__init__(*args, **kwargs)

    def __str__(self):
        return f"{self.name} is {self.age} years old"
