package com.example.demo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ControllerB {

    @Autowired
    private RestTemplate restTemplate;

    public String test(User user) {
        List<Object> params = new ArrayList();
        params.add(user);
        RequestWrapper wrapper = new RequestWrapper("com.example.demo.ControllerB.test(com.example.demo.User)", params);
        return restTemplate.postForObject("http://service-b/mapper", wrapper, String.class);
    }
}
