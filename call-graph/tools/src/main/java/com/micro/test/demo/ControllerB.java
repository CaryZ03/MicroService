package com.micro.test.demo;

import org.springframework.web.bind.annotation.*;

@RestController
public class ControllerB {

    public String test(User user) {
        return "Get result from service B. User: " + user.getId() + user.getUsername();
    }

}
