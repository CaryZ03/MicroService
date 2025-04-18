package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerB {

    @PostMapping("/B/test")
    public String test(@RequestBody User user) {
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }

}
