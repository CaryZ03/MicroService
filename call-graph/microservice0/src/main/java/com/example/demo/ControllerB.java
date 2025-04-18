package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
public class ControllerB {

    @GetMapping("/testA")
    public String test(@RequestParam User user) {
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }

    @PostMapping("/testA")
    public String testi(@RequestBody User user) {
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }

}
