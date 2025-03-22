package com.example.testProject.api;

import com.example.testProject.repository.Add;
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
            return ApiResponse.error(400, "Invalid input parameters");
        } catch (Exception e) {
            return ApiResponse.error(500, "Internal server error");
        }
    }
}
