import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.Modifier;
import java.util.regex.*;

public class CodeMerger {
    private static final Pattern MARKDOWN_CODE_BLOCK = Pattern.compile("```java\\n(.*?)\\n```", Pattern.DOTALL);

    public static String mergeCode(String original, String generated) {
        try {
            // 预处理生成的代码
            String cleanedCode = cleanGeneratedCode(generated);

            // 配置支持Java 17语法
            ParserConfiguration config = new ParserConfiguration()
                    .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
            StaticJavaParser.setConfiguration(config);

            // 解析代码
            CompilationUnit originalCu = StaticJavaParser.parse(original);
            CompilationUnit generatedCu = StaticJavaParser.parse(cleanedCode);

            // 提取生成的API端点
            ClassOrInterfaceDeclaration apiController = generatedCu.getClassByName("CalculateController")
                    .orElseThrow(() -> new RuntimeException("未找到生成控制器"));

            // 克隆节点避免冲突
            ClassOrInterfaceDeclaration clonedController = apiController.clone();

            // 添加类型到原始文件（带有包声明处理）
            originalCu.getPackageDeclaration().ifPresentOrElse(
                    pkg -> originalCu.addType(clonedController),
                    () -> originalCu.addType(clonedController)
            );

            // 修改原方法为私有
            originalCu.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals("calculate"))
                    .forEach(m -> {
                        m.getModifiers().clear();
                        m.addModifier(Modifier.Keyword.PRIVATE);
                    });

            return originalCu.toString();
        } catch (ParseProblemException e) {
            throw new RuntimeException("代码解析失败，请检查生成内容：\n" + generated, e);
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