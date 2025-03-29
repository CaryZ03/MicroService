package com.example.testProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.testProject.repository.Add;

@RestController
public class NumberController {

    @GetMapping("/number")
    public int getNumber() {
        // int ans = new RestTemplate().postForObject("http://service-host/api/v1/add", Map.of("arg0", 1, "arg1", 2), ApiResponse.class).getData();
        int ans = new Add().add(1, 2);
        return ans;
    }
}
