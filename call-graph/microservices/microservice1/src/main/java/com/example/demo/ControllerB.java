package com.example.demo;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerB {

    @GetMapping("/test")
    public String test() {
        return "Get result from service B.";
    }

}
