package codegen;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.resolution.types.ResolvedType;

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

    public static void replaceMethodCalls(Path sourceFile,
            String methodName,
            int targetParamCount,
            List<String> paramTypes, // 新增参数类型
            String returnType,
            List<String> paramNames) throws IOException {
        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(sourceFile));

        // 包排除逻辑（保留原有）
        String generatedPackage = "com.example.testProject.api";
        Optional<String> packageOpt = cu.getPackageDeclaration().map(pd -> pd.getNameAsString());
        if (packageOpt.isPresent() && packageOpt.get().equals(generatedPackage)) {
            System.out.println("跳过生成包中的文件: " + sourceFile);
            return;
        }

        // 添加必要的import
        addImportIfMissing(cu, "org.springframework.web.client.RestTemplate");
        addImportIfMissing(cu, "com.example.testProject.api.ApiResponse");
        addImportIfMissing(cu, "java.util.Map");

        // 遍历所有方法调用表达式
        cu.findAll(MethodCallExpr.class).stream()
                .filter(mce -> {
                    // 1. 方法名匹配
                    if (!mce.getNameAsString().equals(methodName))
                        return false;

                    // 2. 参数数量匹配
                    if (mce.getArguments().size() != targetParamCount)
                        return false;

                    // TODO: 启动参数类型匹配
                    // // 3. 参数类型匹配
                    // for (int i = 0; i < targetParamCount; i++) {
                    // Expression arg = mce.getArgument(i);
                    // String actualType = getArgumentType(arg, paramTypes, i);
                    // String expectedType = paramTypes.get(i);

                    // // 宽松匹配：允许int与Integer等基本/包装类型互换
                    // if (!isTypeMatch(actualType, expectedType)) {
                    // return false;
                    // }
                    // }
                    return true;
                })
                .forEach(mce -> {
                    // 提取实际参数表达式
                    List<String> actualArgs = mce.getArguments().stream()
                            .map(arg -> arg.toString())
                            .collect(Collectors.toList());

                    // 构建参数映射（使用形参名称）
                    List<String> params = new ArrayList<>();
                    for (int i = 0; i < actualArgs.size(); i++) {
                        params.add(String.format("\"%s\", %s", paramNames.get(i), actualArgs.get(i)));
                    }

                    // 构建新的调用表达式
                    String newCall = String.format(
                            "(%s) new RestTemplate().postForObject(\"%s\", Map.of(%s), ApiResponse.class).getData()",
                            returnType,
                            buildApiUrl(methodName),
                            String.join(", ", params));

                    mce.replace(StaticJavaParser.parseExpression(newCall));
                });

        Files.write(sourceFile, cu.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static boolean isTypeMatch(String actualType, String expectedType) {
        // 统一小写比较（避免String与string不一致）
        actualType = actualType.toLowerCase();
        expectedType = expectedType.toLowerCase();

        // 定义类型兼容映射
        Map<String, Set<String>> compatibleTypes = Map.of(
                "int", Set.of("int", "integer"),
                "double", Set.of("double"),
                "boolean", Set.of("boolean", "bool"),
                "string", Set.of("string"),
                "long", Set.of("long"));

        // 检查是否兼容
        return compatibleTypes.getOrDefault(expectedType, Set.of(expectedType))
                .contains(actualType);
    }

    private static String getArgumentType(Expression arg, List<String> paramTypes, int index) {
        // 处理字面量类型
        if (arg instanceof IntegerLiteralExpr) {
            return "int";
        } else if (arg instanceof DoubleLiteralExpr) {
            return "double";
        } else if (arg instanceof BooleanLiteralExpr) {
            return "boolean";
        } else if (arg instanceof StringLiteralExpr) {
            return "String";
        } else if (arg instanceof LongLiteralExpr) {
            return "long";
        } else if (arg instanceof CharLiteralExpr) {
            return "char";
        } else if (arg instanceof NullLiteralExpr) {
            // 根据目标形参类型推断（如形参为String则返回String）
            return paramTypes.get(index);
        }

        // 非字面量：尝试解析类型
        try {
            ResolvedType resolvedType = arg.calculateResolvedType();
            String typeName = resolvedType.describe();

            // 将包装类型转为基本类型（如java.lang.Integer → int）
            return typeName.replace("java.lang.", "")
                    .replace("Integer", "int")
                    .replace("Double", "double")
                    .replace("Boolean", "boolean")
                    .replace("Long", "long")
                    .replace("Character", "char");
        } catch (Exception e) {
            return "unknown"; // 解析失败时标记为未知
        }
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