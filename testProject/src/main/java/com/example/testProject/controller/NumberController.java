package com.example.testProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.testProject.repository.Add;

@RestController
public class NumberController {

    @GetMapping("/number")
    public int getNumber() {
        Add add = new Add();
        int ans = add.add(1, 2);
        return ans; // 这里可以替换为任何你想要的数字
    }
}