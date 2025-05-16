package com.example.demo;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerB {

    public String test(User user) {
        user.setId(user.getId() + 1);
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }

}
