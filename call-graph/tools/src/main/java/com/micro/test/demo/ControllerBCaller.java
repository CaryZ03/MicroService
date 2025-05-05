package com.micro.test.demo;

import com.micro.test.utils.RequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ControllerBCaller {

    @Autowired
    private RestTemplate restTemplate;

    public String test(User user) {

        String SERVICE_PROVIDER_ADDRESS = "http://127.0.0.1:18555";

        List<Object> params = new ArrayList<>();

        params.add(user);

        RequestWrapper wrapper = new RequestWrapper("com.micro.test.demo.ControllerB.test(com.micro.test.demo.User)", params);

        return restTemplate.postForObject(SERVICE_PROVIDER_ADDRESS +"/mapper", wrapper, String.class);
    }

}
