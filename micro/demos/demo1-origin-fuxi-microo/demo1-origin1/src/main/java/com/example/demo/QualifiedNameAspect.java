//package com.micro.test.utils;
//
//import com.example.demo.RequestWrapper;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@Aspect
//@Component
//public class QualifiedNameAspect {
//
//    @Autowired
//    FunctionMap functionMap;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    private final List<Object[]> methodArgsList = new CopyOnWriteArrayList<>();
//
//    public QualifiedNameAspect(FunctionMap functionMap) {
//        this.functionMap = functionMap;
//    }
//
//    @Around("@annotation(LogQualifiedName)")
//    public Object logMethodName(ProceedingJoinPoint joinPoint) throws Throwable {
//        String className = joinPoint.getSignature().getDeclaringTypeName();
//        String methodName = joinPoint.getSignature().getName();
//        String fullQualifiedName = className + "." + methodName;
//
//        Object[] args = joinPoint.getArgs();
//
//        List<Object> params = new ArrayList();
//        Collections.addAll(params, args);
//
//        List<String> args_names = new ArrayList<>();
//
//        for (Object arg : args) {
//            args_names.add(arg.getClass().getName());
//        }
//        String wrapperName = fullQualifiedName + "(" + String.join(",", args_names) + ")";
//
//        String remoteUrl = functionMap.getMap().get(fullQualifiedName);
//        if (Objects.equals(remoteUrl, "localhost")) {
//            System.out.println("调用本地方法");
//            return joinPoint.proceed(); // 继续执行原方法
//        } else {
//            System.out.println("调用远程方法");
//            RequestWrapper wrapper = new RequestWrapper(wrapperName, params);
//            return restTemplate.postForObject(remoteUrl + "/mapper", wrapper, String.class);
//        }
//    }
//
//}