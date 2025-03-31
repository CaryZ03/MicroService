package com.example.testProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.testProject.repository.Add;
import org.springframework.web.client.RestTemplate;
import com.example.testProject.api.ApiResponse;
import java.util.Map;

@RestController
public class NumberController {

    @SuppressWarnings("null")
    @GetMapping("/number")
    public int getNumber() {
        // int ans = new RestTemplate().postForObject("http://service-host/api/v1/add",
        // Map.of("arg0", 1, "arg1", 2), ApiResponse.class).getData();
        int ans = (int) new RestTemplate()
                .postForObject("http://service-host/api/v1/add", Map.of("a", 1, "b", 2), ApiResponse.class).getData();
        int ans2 = (int) new RestTemplate()
                .postForObject("http://service-host/api/v1/add",
                        Map.of("a",
                                (int) new RestTemplate().postForObject("http://service-host/api/v1/add",
                                        Map.of("a", 1, "b", 2), ApiResponse.class).getData(),
                                "b", 2, "c", 3),
                        ApiResponse.class)
                .getData();
        return ans + ans2;
    }
}
