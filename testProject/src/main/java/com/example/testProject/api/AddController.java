package com.example.testProject.api;

import org.springframework.web.bind.annotation.*;
import com.example.testProject.repository.Add;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AddController {

    @PostMapping("/add/int-int")
    public ApiResponse<Integer> addTwoInts(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int a = (int) body.get("a");
        int b = (int) body.get("b");
        int result = new Add().add(a, b);
        return ApiResponse.success(result);
    }

    @PostMapping("/add/int-int-int")
    public ApiResponse<Integer> addThreeInts(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int a = (int) body.get("a");
        int b = (int) body.get("b");
        int c = (int) body.get("c");
        int result = new Add().add(a, b, c);
        return ApiResponse.success(result);
    }

    @PostMapping("/add/int-int-int-int")
    public ApiResponse<Integer> addFourInts(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int a = (int) body.get("a");
        int b = (int) body.get("b");
        int c = (int) body.get("c");
        int d = (int) body.get("d");
        int result = new Add().add(a, b, c, d);
        return ApiResponse.success(result);
    }

    @PostMapping("/add/int-int-int-int-int")
    public ApiResponse<Integer> addFiveInts(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int a = (int) body.get("a");
        int b = (int) body.get("b");
        int c = (int) body.get("c");
        int d = (int) body.get("d");
        int e = (int) body.get("e");
        int result = new Add().add(a, b, c, d, e);
        return ApiResponse.success(result);
    }

    @PostMapping("/add/int-double")
    public ApiResponse<Integer> addIntDouble(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int x = (int) body.get("x");
        double y = (double) body.get("y");
        int result = new Add().add(x, y);
        return ApiResponse.success(result);
    }

    @PostMapping("/add/int-double-int")
    public ApiResponse<Integer> addIntDoubleInt(@RequestBody ApiRequest request) {
        Map<String, Object> body = request.getBody();
        int x = (int) body.get("x");
        double y = (double) body.get("y");
        int z = (int) body.get("z");
        int result = new Add().add(x, y, z);
        return ApiResponse.success(result);
    }
}
