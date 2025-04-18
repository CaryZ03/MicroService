import requests
import zipfile
import io
import json

def createProject(baseDir):
    # baseDir = "demo1"
    groupId = "com.example"
    artifactId = "demo"
    name = artifactId
    packageName = groupId + '.' + artifactId


    # 定义请求参数
    url = "https://start.spring.io/starter.zip"
    params = {
        "type": "maven-project",
        "language" : "java",
        "bootVersion" : "3.4.4",
        "baseDir": baseDir,
        "groupId": groupId,
        "artifactId": artifactId,
        "name": name,
        "description": "Demo project for Spring Boot",
        "packageName": packageName,
        "packaging": "jar",
        "javaVersion": "24"
    }

    # D:\Projects\MicroService\work\myapp\{baseDir}\src\main\java\{packageName}\{name}Application.java

    # 发送请求并下载文件
    response = requests.get(url, params=params, stream=True)
    if response.status_code == 200:
        # 使用 io.BytesIO 将响应内容转换为类文件对象
        zip_file = zipfile.ZipFile(io.BytesIO(response.content))
        
        # 解压文件
        extract_to_path = "microservices"
        zip_file.extractall(extract_to_path)
        
        print("Spring Boot Maven project downloaded and extracted successfully.")
    else:
        print("Failed to download the Spring Boot Maven project.")
        print("Status code:", response.status_code)


jsonFile = "../microservices1.json"
with open(jsonFile, 'r') as file:
    data = json.load(file)

print(data)

for key, value in data.items():
    createProject("demo" + key)


# for i in range(1, 3):
#     createProject("demo" + str(i))






# \myapp\demo\src\main\java\
