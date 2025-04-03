package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Trace
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/username/{username}")
    @Trace
    public List<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/id/{id}")
    @Trace
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}