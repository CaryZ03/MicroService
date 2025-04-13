package com.micro.javaparser.demo;

import org.apache.skywalking.apm.toolkit.trace.Trace;

public class Example2 {

    @Trace()
    public void method3() {
        System.out.println("Hello, World!");
        method4();
    }

    @Trace()
    public void method4() {
        System.out.println("Another method");
    }
}
