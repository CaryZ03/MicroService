package com.example.demo;

import org.springframework.web.bind.annotation.RestController;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@RestController
public class ControllerB {

    @Trace
    public String test(User user) {
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }
}
