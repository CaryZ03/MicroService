package codegen;
// sk-9496fcd1ba2849d49ff8da5e4d0e3a9c

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeepSeekApiClient {
    private static final String COMPLETION_API = "https://api.deepseek.com/beta/completions";
    private static final String CHAT_API = "https://api.deepseek.com/beta/chat/completions";
    private static final String API_KEY = "sk-9496fcd1ba2849d49ff8da5e4d0e3a9c";
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
            .readTimeout(60, TimeUnit.SECONDS)     // 读取响应超时
            .writeTimeout(30, TimeUnit.SECONDS)    // 发送请求超时
            .build();
    private static final String PACKAGE_NAME = "com.example.testProject.api";

    /**
     * 将指定函数转换为API接口
     *
     * @param originalCode 原始函数代码
     * @return 生成的API服务端代码
     */
    public static String generateServiceCode(String originalCode) throws IOException {
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
                4. 错误处理：
                   - 校验失败返回400错误
                   - 业务异常返回500错误
                5. 只返回Java代码，不要解释
                

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
        String prompt = String.format(template, PACKAGE_NAME, originalCode);

        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(new Message("system", "你是一个Java微服务架构专家，专门生成Spring风格的API客户端代码"), new Message("user", prompt));
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
                将以下本地方法调用转换为使用RestTemplate的API调用，严格遵循以下要求：
                            
                # 规范要求
                1. 使用Spring的RestTemplate
                2. 参数通过URL参数传递（POST请求）
                3. 响应结构使用ApiResponse类：
                   class ApiResponse<T> {
                       private int code;
                       private String message;
                       private T data;
                       // getters
                   }
                4. 返回代码结构示例：
                    public {ReturnType} {methodName}(...) {
                        RestTemplate restTemplate = new RestTemplate();
                        // 请求逻辑
                    }
                5. 只返回Java代码，不要解释
                               
                # 输入调用：
                %s
                 
                 """;

        String prompt = String.format(template, originalCall);

        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(new Message("system", "你是一个Java微服务架构专家，专门生成Spring风格的API客户端代码"), new Message("user", prompt));
        request.max_tokens = 1024;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }


    public static String performCodeOperation(CodeOperation operation, String code,
                                              String additionalParams) throws IOException {
        switch (operation) {
            case CODE_COMPLETION:
                return handleCodeCompletion(code);
            case CODE_EXPLANATION:
                return handleCodeExplanation(code);
            case TEST_GENERATION:
                return handleTestGeneration(code);
            case CODE_REFACTOR:
                return handleCodeRefactor(code, additionalParams);
            case FUNCTION_TO_API:
                return generateServiceCode(code);
            default:
                throw new IllegalArgumentException("Unsupported operation");
        }
    }

    private static String handleCodeCompletion(String code) throws IOException {
        int splitPoint = code.length() / 2;
        String prefix = code.substring(0, splitPoint);
        String suffix = code.substring(splitPoint);

        CompletionRequest request = new CompletionRequest();
        request.model = "deepseek-chat";
        request.prompt = prefix;
        request.suffix = suffix;
        request.max_tokens = 1024;
        request.temperature = 0;

        String response = sendRequest(COMPLETION_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return prefix + resp.text + suffix;
    }

    private static String handleCodeExplanation(String code) throws IOException {
        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(
                new Message("user", "请解释以下代码：\n" + code)
        );
        request.max_tokens = 512;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }

    private static String handleTestGeneration(String code) throws IOException {
        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(
                new Message("user", "为以下代码生成JUnit测试：\n" + code)
        );
        request.max_tokens = 1024;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }

    private static String handleCodeRefactor(String code, String instruction) throws IOException {
        ChatRequest request = new ChatRequest();
        request.model = "deepseek-chat";
        request.messages = List.of(
                new Message("user", "根据以下要求重构代码：" + instruction + "\n代码：\n" + code)
        );
        request.max_tokens = 1024;

        String response = sendRequest(CHAT_API, request);
        ApiResponse resp = gson.fromJson(response, ApiResponse.class);
        return resp.choices.get(0).message.content;
    }

    private static String sendRequest(String url, Object requestBody) throws IOException {
        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

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

    public static void main(String[] args) {
        try {
            if (!isNetworkAvailable()) {
                System.err.println("网络不可用，请检查连接");
                return;
            }

            String SAMPLE_FUNCTION =
                    "public int calculate(int a, int b) {\n"
                            + "    // 复杂业务逻辑\n"
                            + "    return a * b + 1024;\n"
                            + "}";

            String SAMPLE_CALL =
                    "int result = calculator.calculate(5, 8);";

//            // 使用示例
//            System.out.println("解释结果：");
//            System.out.println(performCodeOperation(CodeOperation.CODE_EXPLANATION, code, null));
//
//            System.out.println("\n测试用例：");
//            System.out.println(performCodeOperation(CodeOperation.TEST_GENERATION, code, null));
//
//            System.out.println("\n重构结果：");
//            System.out.println(performCodeOperation(CodeOperation.CODE_REFACTOR, code, "添加参数校验"));

            System.out.println("\nAPI服务端代码：");
            System.out.println(generateServiceCode(SAMPLE_FUNCTION));

            System.out.println("\nAPI客户端代码：");
            System.out.println(generateClientCode(SAMPLE_CALL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNetworkAvailable() {
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