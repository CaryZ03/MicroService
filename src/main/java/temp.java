public class temp {
    public static void main(String[] args) {
        String a = "将以下本地方法调用转换为使用RestTemplate的API调用，严格遵循以下要求：\n\n"
                + "# 规范要求\n"
                + "1. 使用Spring的RestTemplate\n"
                + "2. 参数通过URL参数传递（GET请求）\n"
                + "3. 错误处理要求：\n"
                + "   - 处理4xx/5xx状态码\n"
                + "   - 处理通用异常\n"
                + "4. 响应结构使用ApiResponse类：\n"
                + "   class ApiResponse<T> {\n"
                + "       private int code;\n"
                + "       private String message;\n"
                + "       private T data;\n"
                + "       // getters\n"
                + "   }\n"
                + "5. 返回代码结构示例：\n"
                + "public class {ServiceName} {\n"
                + "    public {ReturnType} {methodName}(...) {\n"
                + "        RestTemplate restTemplate = new RestTemplate();\n"
                + "        // 请求逻辑\n"
                + "    }\n"
                + "}\n\n"
                + "# 输入调用：\n%s";
        System.out.println(a);
    }
}
