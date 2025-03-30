import requests
import zipfile
import io

# 定义请求参数
url = "https://start.spring.io/starter.zip"
params = {
    "type": "maven-project",
    "language" : "java",
    "bootVersion" : "3.4.4",
    "baseDir": "demo3",
    # "groupId": "com.example",
    # "artifactId": "hihi",
    "name": "demo3",
    "description": "Demo project for Spring Boot",
    "packageName": "com.eample.hihi",
    "packaging": "jar",
    "javaVersion": "17"
}

# D:\Projects\MicroService\work\myapp\{baseDir}\src\main\java\{groupId}\demo

# 发送请求并下载文件
response = requests.get(url, params=params, stream=True)
if response.status_code == 200:
    # 使用 io.BytesIO 将响应内容转换为类文件对象
    zip_file = zipfile.ZipFile(io.BytesIO(response.content))
    
    # 解压文件
    extract_to_path = "myapp"
    zip_file.extractall(extract_to_path)
    
    print("Spring Boot Maven project downloaded and extracted successfully.")
else:
    print("Failed to download the Spring Boot Maven project.")
    print("Status code:", response.status_code)







# \myapp\demo\src\main\java\
