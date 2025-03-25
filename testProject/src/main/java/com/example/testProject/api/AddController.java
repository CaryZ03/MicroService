package com.example.testProject.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AddController {

    @PostMapping("/add")
    public ApiResponse<Integer> add(@RequestParam int a, @RequestParam int b) {
        try {
            int result = new Add().add(a, b);
            return ApiResponse.success(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, "Internal server error");
        }
    }
}
