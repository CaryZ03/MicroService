package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@SpringBootApplication
public class Demo1Application {

    @Trace
    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }
}
