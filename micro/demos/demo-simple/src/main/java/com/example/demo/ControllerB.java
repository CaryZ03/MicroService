package com.example.demo;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerB {

    public String test(String name) {
        return "Get result from service B." + name;
    }

}
