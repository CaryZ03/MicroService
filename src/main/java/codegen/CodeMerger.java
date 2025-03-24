package codegen;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class CodeMerger {
    private static final Pattern MARKDOWN_CODE_BLOCK = Pattern.compile("```java\\n(.*?)\\n```", Pattern.DOTALL);

    @SuppressWarnings("deprecation")
    public static String writeController(String original, String generated, String projectRoot) {
        try {
            String cleanedCode = cleanGeneratedCode(generated);
            System.out.println("cleanedCode: " + cleanedCode);
            StaticJavaParser.getConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

            CompilationUnit generatedCu = StaticJavaParser.parse(cleanedCode);
            String packageName = generatedCu.getPackageDeclaration()
                    .map(pd -> pd.getNameAsString())
                    .orElse("cyx: if you see this,it's wrong from CodeMerger"); // 默认包名

            // 提取控制器类
    //        ClassOrInterfaceDeclaration controller = generatedCu.getClassByName(".*Controller")
    //                .orElseThrow(() -> new RuntimeException("未找到生成的控制器类"));
            Optional<ClassOrInterfaceDeclaration> controllerOpt = generatedCu
                    .findAll(ClassOrInterfaceDeclaration.class)
                    .stream()
                    .filter(c -> c.getNameAsString().endsWith("Controller"))
                    .findFirst();

            ClassOrInterfaceDeclaration controller = controllerOpt.orElseThrow(
                    () -> new RuntimeException("未找到Controller类，请检查生成代码是否符合命名规范（以Controller结尾）")
            );

            // 构建目标路径
            Path outputPath = Paths.get(projectRoot, "src/main/java",
                    packageName.replace(".", File.separator), controller.getNameAsString() + ".java");

            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, generatedCu.toString().getBytes(), StandardOpenOption.CREATE);

            return cleanedCode; // 原始文件不需要修改
        } catch (Exception e) {
            throw new RuntimeException("代码合并失败: " + e.getMessage());
        }
    }

    // 新增客户端调用合并方法
    public static void replaceMethodCalls(Path sourceFile, String methodName, String generatedCode) throws IOException {
        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(sourceFile));
        
        // 1. 添加必要的import
        if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals("org.springframework.web.client.RestTemplate"))) {
            cu.addImport("org.springframework.web.client.RestTemplate").addImport("import com.example.testProject.api.ApiResponse;");
        }
        if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals("java.util.Map"))) {
            cu.addImport("java.util.Map");
        }

        // 2. 转换方法调用
        cu.findAll(MethodCallExpr.class).stream()
            .filter(mce -> mce.getNameAsString().equals(methodName))
            .forEach(mce -> {
                // 构建新的方法调用表达式
                String args = mce.getArguments().stream()
                    .map(arg -> arg.toString())
                    .collect(Collectors.joining(", "));

                String newCall = String.format(
                    "new RestTemplate().postForObject(\"%s\", Map.of(%s), ApiResponse.class).getData()",
                    buildApiUrl(methodName),
                    buildParameters(mce.getArguments())
                );

                // 替换原始调用
                mce.replace(StaticJavaParser.parseExpression(newCall));
            });

        // 3. 保存修改后的文件
        Files.write(sourceFile, cu.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String buildApiUrl(String methodName) {
        return String.format("http://service-host/api/v1/%s", methodNameToPath(methodName));
    }

    private static String methodNameToPath(String methodName) {
        return methodName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
    }

    private static String buildParameters(NodeList<Expression> arguments) {
        List<String> params = new ArrayList<>();
        for (int i = 0; i < arguments.size(); i++) {
            params.add(String.format("\"arg%d\", %s", i, arguments.get(i)));
        }
        return String.join(", ", params);
    }


    private static String cleanGeneratedCode(String generated) {
        // 清理Markdown代码块
        Matcher matcher = MARKDOWN_CODE_BLOCK.matcher(generated);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // 清理其他非法字符
        return generated.replaceAll("[`\\\\]", "")
                .replaceAll("(?m)^\\s*//.*", "") // 删除行注释
                .trim();
    }
}