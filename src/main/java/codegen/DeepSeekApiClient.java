package codegen;
// sk-9496fcd1ba2849d49ff8da5e4d0e3a9c

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeepSeekApiClient {
    private static final String CHAT_API = "https://api.deepseek.com/beta/chat/completions";
    private static final String API_KEY = "sk-9496fcd1ba2849d49ff8da5e4d0e3a9c";
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
            .readTimeout(60, TimeUnit.SECONDS) // 读取响应超时
            .writeTimeout(30, TimeUnit.SECONDS) // 发送请求超时
            .build();
    private static final String PACKAGE_NAME = "com.example.testProject.api";

    /**
     * 将指定函数转换为API接口
     *
     * @param originalCode 原始函数代码
     * @return 生成的API服务端代码
     */
    public static String generateServiceCode(Path targetFile, String originalCode) throws IOException {
        String template = """
                        请将以下Java函数转换为Spring Boot REST API端点，严格遵循以下规范：

                        # 规范要求
                        1. 使用统一响应结构，这个结构已经存在于com.example.testProject/api/ApiResponse.java，你可以直接调用，不需要import：
                           class ApiResponse<T> {
                               int code;
                               String message;
                               T data;
                               // 构造器和静态工厂方法
                           }
                        2. 接口路径前缀为/api/v1
                        3. 新的类名为改造的函数名+Controller，它位于%s包下
                        4. 只返回Java代码，不要解释
                        5. 该API接口将直接调用原始函数(使用new来新建一个实例)，不需要实现原始函数逻辑，
                            原始函数位于%s文件中，请你注意import
                        6. 使用统一请求体结构，这个结构已经存在于com.example.testProject/api/ApiRequest.java，你可以直接调用，不需要import：
                           class ApiRequest {
                               String path;
                               final String method = "POST";
                               Map<String, Object> body;
                               // 构造器和getter/setter
                           }
                        7. URL路径格式：/方法名/参数类型1-参数类型2（全小写，基本类型使用简称）
                        示例：
                        - add(int, int) → /add/int-int
                        - calculate(double, String) → /calculate/double-string
                        8. 路径中的类型简称映射：
                        int→int, double→double, String→string, boolean→bool
                        # 输入方法：
                        %s

                        # 返回示例：
                package api;

                import org.springframework.web.bind.annotation.*;

                @RestController
                @RequestMapping("/api/v1")
                public class CalculateController {
                    @PostMapping("/calculate")
                    public ApiResponse<Integer> calculateMethod(@Valid @RequestBody RequestDto dto) {
                        int result = OriginalClass.calculate(dto.getA(), dto.getB());
                        return ApiResponse.success(result);
                    }
                }
                """;
        String prompt = String.format(template, PACKAGE_NAME, targetFile, originalCode);

        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(new Message("system", "你是一个Java微服务架构专家，专门生成Spring风格的API客户端代码"),
                new Message("user", prompt));
        request.max_tokens = 2048;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }

    /**
     * 生成API客户端代码
     *
     * @param originalCall 原始调用代码
     * @return 生成的API调用代码
     */
    public static String generateClientCode(String originalCall) throws IOException {
        String template = """
                请将以下方法调用转换为直接接口调用代码，要求：
                1. 使用RestTemplate直接发起POST请求
                2. 保持单行调用结构
                3. 参数按顺序转为JSON请求体
                4. 自动提取返回值的data字段
                5. 只返回Java代码，不要解释

                示例输入：
                int result = calculator.calculate(5, 8);

                示例输出：
                int result = restTemplate.postForObject(
                    "http://service-host/api/v1/calculate",
                    Map.of("a", a, "b", b),
                    ApiResponse.class
                ).getData();

                需要转换的原始调用：
                %s
                """;

        String prompt = String.format(template, originalCall);

        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(new Message("system", "你是一个Java微服务架构专家，专门生成内联接口调用代码"), new Message("user", prompt));
        request.max_tokens = 1024;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }

    /*
     * 发送请求，辅助方法
     * 
     * @param url 请求地址
     * 
     * @param requestBody 请求体
     * 
     * @return 响应体
     */
    private static String sendRequest(String url, Object requestBody) throws IOException {
        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Request failed: " + response);
            }
            return response.body().string();
        }
    }

    /*
     * 检查网络是否可用
     * 
     * @return 网络是否可用
     */
    public static boolean isNetworkAvailable() {
        try {
            OkHttpClient tempClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.baidu.com") // 测试连通性的地址
                    .build();

            Response response = tempClient.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }
}