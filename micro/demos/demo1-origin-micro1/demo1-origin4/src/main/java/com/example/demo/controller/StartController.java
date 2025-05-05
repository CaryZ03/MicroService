package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/start")
public class StartController {

    @RequestMapping("/test")
    public String startSpringBoot() {
        return "hihi";
    }
}
