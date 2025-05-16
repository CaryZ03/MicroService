package com.micro.test.utils;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Method;
import java.util.List;

@RestController
public class RequestMapper {

    @Autowired
    private ApplicationContext applicationContext;

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
                paramsArray[i] = gson.fromJson(gson.toJson(params.get(i)), paramTypes[i]);
            }
            Method targetMethod = targetClass.getMethod(methodName, paramTypes);
            Object serviceInstance = applicationContext.getBean(targetClass);
            return targetMethod.invoke(serviceInstance, paramsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
