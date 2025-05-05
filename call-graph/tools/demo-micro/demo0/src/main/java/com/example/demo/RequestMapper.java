package com.example.demo;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RestController
public class RequestMapper {

    @PostMapping("/mapper")
    public Object mapper(@RequestBody RequestWrapper wrapper) {
        String path = wrapper.getPath();
        List<Object> params = wrapper.getParams();
        String methodPath = path.substring(0, path.indexOf('('));
        String classPath = methodPath.substring(0, methodPath.lastIndexOf('.'));
        String methodName = methodPath.substring(methodPath.lastIndexOf(".") + 1);
        String[] parameters = path.substring(path.indexOf('(') + 1, path.indexOf(')')).split(",");
        try {
            Class<?> targetClass = Class.forName(classPath);
            Class<?>[] paramTypes = new Class<?>[parameters.length];
            Object[] paramsArray = new Object[parameters.length];
            Gson gson = new Gson();
            for (int i = 0; i < parameters.length; i++) {
                paramTypes[i] = Class.forName(parameters[i].trim());
                // 如果参数是Map类型，将其转换为目标类型
                if (params.get(i) instanceof Map) {
                    paramsArray[i] = gson.fromJson(gson.toJson(params.get(i)), paramTypes[i]);
                } else {
                    paramsArray[i] = params.get(i);
                }
            }
            Method targetMethod = targetClass.getMethod(methodName, paramTypes);
            Object serviceInstance = targetClass.getDeclaredConstructor().newInstance();
            // 输出调试信息
            System.out.println("Target Class: " + targetClass.getName());
            System.out.println("Target Method: " + targetMethod.getName());
            System.out.println("Parameter Types: " + java.util.Arrays.toString(paramTypes));
            System.out.println("Parameters: " + java.util.Arrays.toString(paramsArray));
            return targetMethod.invoke(serviceInstance, paramsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
