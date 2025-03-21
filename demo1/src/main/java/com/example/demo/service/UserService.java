package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Trace
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Trace
    public List<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Trace
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}