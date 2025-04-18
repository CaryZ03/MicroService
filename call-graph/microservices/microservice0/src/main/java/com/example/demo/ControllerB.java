package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ControllerB {
    
    @Autowired
    private RestTemplate restTemplate;

    public String test(User user) {

        String SERVICE_PROVIDER_ADDRESS = "http://127.0.0.1:18001";

        return restTemplate.postForObject(SERVICE_PROVIDER_ADDRESS +"/B/test", user, String.class);
    }

}
