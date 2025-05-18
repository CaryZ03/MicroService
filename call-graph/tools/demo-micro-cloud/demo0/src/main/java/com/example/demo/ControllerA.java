package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerA {

    @Autowired
    ControllerB controllerB = new ControllerB();

    @GetMapping("/test")
    public String callServiceB() {
        User user = new User();
        user.setId(1L);
        user.setUsername("hihi");

        return controllerB.test(user);
    }

}
