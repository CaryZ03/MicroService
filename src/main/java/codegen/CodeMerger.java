package codegen;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.*;

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