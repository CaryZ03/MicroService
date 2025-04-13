package com.micro.javaparser.demo;

import org.apache.skywalking.apm.toolkit.trace.Trace;

public class Example {

    @Trace()
    public void method1() {
        System.out.println("Hello, World!");
        method2();
    }

    @Trace()
    public void method2() {
        System.out.println("Another method");
        Example2 example2 = new Example2();
        example2.method3();
    }
}
