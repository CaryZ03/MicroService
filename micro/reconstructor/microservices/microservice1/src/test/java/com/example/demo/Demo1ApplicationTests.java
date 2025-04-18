package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@SpringBootTest
class Demo1ApplicationTests {

    @Test
    @Trace()
    void contextLoads() {
    }
}
