package codegen;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
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
            // ClassOrInterfaceDeclaration controller =
            // generatedCu.getClassByName(".*Controller")
            // .orElseThrow(() -> new RuntimeException("未找到生成的控制器类"));
            Optional<ClassOrInterfaceDeclaration> controllerOpt = generatedCu
                    .findAll(ClassOrInterfaceDeclaration.class)
                    .stream()
                    .filter(c -> c.getNameAsString().endsWith("Controller"))
                    .findFirst();

            ClassOrInterfaceDeclaration controller = controllerOpt.orElseThrow(
                    () -> new RuntimeException("未找到Controller类，请检查生成代码是否符合命名规范（以Controller结尾）"));

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

    public static void replaceMethodCalls(Path sourceFile, String methodName,
            String returnType, List<String> paramNames) throws IOException {
        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(sourceFile));

        // 1. 添加必要的import
        addImportIfMissing(cu, "org.springframework.web.client.RestTemplate");
        addImportIfMissing(cu, "com.example.testProject.api.ApiResponse");
        addImportIfMissing(cu, "java.util.Map");

        // 2. 转换方法调用
        cu.findAll(MethodCallExpr.class).stream()
                .filter(mce -> mce.getNameAsString().equals(methodName))
                .forEach(mce -> {
                    // 构建类型转换表达式
                    String castExpr = String.format("(%s)", returnType);

                    // 构建参数映射
                    String params = paramNames.stream()
                            .map(name -> "\"" + name + "\", " + name)
                            .collect(Collectors.joining(", "));

                    // 构建新的调用表达式
                    String newCall = String.format(
                            "%snew RestTemplate().postForObject(\"%s\", Map.of(%s), ApiResponse.class).getData()",
                            castExpr,
                            buildApiUrl(methodName),
                            params);

                    // 替换并保留原始参数变量名
                    mce.replace(StaticJavaParser.parseExpression(newCall));
                });

        // 3. 保存修改
        Files.write(sourceFile, cu.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void addImportIfMissing(CompilationUnit cu, String importName) {
        if (cu.getImports().stream().noneMatch(i -> i.getNameAsString().equals(importName))) {
            cu.addImport(importName);
        }
    }

    private static String buildApiUrl(String methodName) {
        // 驼峰转路径（如：calculateSum -> calculate-sum）
        return "http://service-host/api/v1/" +
                methodName.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
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