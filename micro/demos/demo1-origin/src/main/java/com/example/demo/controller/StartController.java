package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.skywalking.apm.toolkit.trace.Trace;

@RestController
@RequestMapping("/start")
public class StartController {

    @RequestMapping("/springboot")
    @Trace()
    public String startSpringBoot() {
        return "hihi";
    }
}
