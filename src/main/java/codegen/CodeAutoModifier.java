package codegen;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class CodeAutoModifier {
    // 配置参数
    private static final String PROJECT_ROOT = "/path/to/project";
    private static final String TARGET_METHOD = "public int calculate(int a, int b)";

    public static void main(String[] args) {
        try {
            // 1. 扫描项目查找目标方法
            Path targetFile = findMethodInProject(PROJECT_ROOT, TARGET_METHOD);
            
            // 2. 读取原始代码
            String originalCode = Files.readString(targetFile);
            
            // 3. 生成新代码
            String modifiedCode = modifyMethod(originalCode, TARGET_METHOD);
            
            // 4. 保存修改
            Files.write(targetFile, modifiedCode.getBytes());
            System.out.println("代码修改成功！");
            
        } catch (Exception e) {
            System.err.println("自动修改失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Path findMethodInProject(String projectPath, String methodSignature) throws Exception {
        return Files.walk(Paths.get(projectPath))
            .filter(p -> p.toString().endsWith(".java"))
            .filter(p -> containsMethod(p, methodSignature))
            .findFirst()
            .orElseThrow(() -> new Exception("未找到目标方法"));
    }

    private static boolean containsMethod(Path filePath, String methodSignature) {
        try {
            // 使用静态解析方法
            JavaParser parser = new JavaParser();
            ParseResult<CompilationUnit> parseResult = parser.parse(filePath);

            CompilationUnit cu = parseResult.getResult().get();
            return cu.findAll(MethodDeclaration.class).stream()
                    .anyMatch(m -> m.getDeclarationAsString().equals(methodSignature));
        } catch (IOException e) {
            return false;
        }
    }
    private static String modifyMethod(String originalCode, String methodSignature) {
        // 使用之前实现的代码生成逻辑
        try {
            return DeepSeekApiClient.generateServiceCode(originalCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}