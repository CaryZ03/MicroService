package com.dofinal.RG.util;

import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * Classname SpringContextHolder
 * Description TODO
 * Date 2024/6/7 20:36
 * Created ZHW
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    @Trace
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Trace
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    @Trace
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }
}
