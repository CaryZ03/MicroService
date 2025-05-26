package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import com.example.demo.RequestWrapper;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Trace
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Trace
    public User getUserByUsername(String username) {
        List<Object> params = new ArrayList();
        params.add(username);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.service.UserService.getUserByUsername(java.lang.String)", params);
        return restTemplate.postForObject("http://127.0.0.1:18500/mapper", wrapper, User.class);
    }

    @Trace
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
