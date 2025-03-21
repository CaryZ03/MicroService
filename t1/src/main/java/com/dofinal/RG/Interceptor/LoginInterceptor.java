package com.dofinal.RG.Interceptor;


import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * &#064;Classname LoginInterceptor
 * &#064;Description  TODO
 * &#064;Date 2024/5/4 20:00
 * &#064;Created MuJue
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Trace
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("I am in interceptor!");
        return true;
    }
}
