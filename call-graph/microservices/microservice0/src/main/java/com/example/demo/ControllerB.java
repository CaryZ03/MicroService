package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ControllerB {

    public String test(String name) {
        RestTemplate restTemplate;

        String SERVICE_PROVIDER_ADDRESS = "http://service-b.example.com:18001";

        return restTemplate.getForObject(SERVICE_PROVIDER_ADDRESS +"/test-service-b", String.class);
    }

}
