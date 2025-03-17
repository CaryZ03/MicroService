public class test1 {

    public static int calculate(int a, int b) {
        return a + b * 1024;
    }
}

@RestController
@RequestMapping("/api/v1")
public class CalculateController {

    @PostMapping("/calculate")
    private ApiResponse<Integer> calculate(@RequestParam @Min(0) int a, @RequestParam @Min(0) int b) {
        int result = a + b * 1024;
        return ApiResponse.success(result);
    }

    public static class ApiResponse<T> {

        private int code;

        private String message;

        private T data;

        public ApiResponse(int code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(200, "Success", data);
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
