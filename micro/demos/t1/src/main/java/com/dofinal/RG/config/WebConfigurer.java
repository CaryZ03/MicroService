package com.dofinal.RG.config;

import com.dofinal.RG.Interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * &#064;Classname WebConfigurer
 * &#064;Description  TODO
 * &#064;Date 2024/5/4 20:06
 * &#064;Created MuJue
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/user/**");
    }
}
