package com.example.testProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.testProject.api.ApiResponse;
import com.example.testProject.repository.Add;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
public class NumberController {

    @GetMapping("/number")
    public int getNumber() {
        Add add = new Add();
        int ans = new RestTemplate().postForObject("http://service-host/api/v1/add", Map.of("arg0", 1, "arg1", 2), ApiResponse.class).getData();
        // 这里可以替换为任何你想要的数字
        return ans;
    }
}
