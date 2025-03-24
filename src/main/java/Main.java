import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import codegen.CodeMerger;
import codegen.DeepSeekApiClient;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    // 配置参数
    private static final String PROJECT_ROOT = "./testProject";
    private static final String TARGET_METHOD = "public int add(int a, int b)";
    private static final String BACKUP_EXT = ".bak";
    private static final String API_BASE_URL = "http://api-service:8080/api/v1";
    private static final String TARGET_METHOD_CALL = "add";

    public static void main(String[] args) {
        try {
            // 1. 查找目标方法所在文件
            Path targetFile = findTargetFile(PROJECT_ROOT, TARGET_METHOD);
            System.out.println("找到目标方法位于: " + targetFile);

            // 2. 创建文件备份
            createBackup(targetFile);
            System.out.println("已创建备份文件: " + targetFile + BACKUP_EXT);

            // 3. 读取原始代码
            String originalCode = Files.readString(targetFile);

            // 4. 生成API代码
            String generatedCode = DeepSeekApiClient.generateServiceCode(originalCode);
            System.out.println("API代码生成成功\n"+generatedCode);

            // 5. 合并代码
            String cleanedCode = CodeMerger.writeController(originalCode, generatedCode, PROJECT_ROOT); // 传递项目根目录

            // 6. 验证语法
            validateSyntax(cleanedCode);

            // 7. 输出结果
            System.out.println("API代码已生成到指定包路径");
        
            // 8. 查找所有包含目标调用的文件
            List<Path> targetFiles = findMethodCallFiles(PROJECT_ROOT, TARGET_METHOD_CALL);
            System.out.println("找到目标调用位于: " + targetFiles);

            for (Path file : targetFiles) {
                // 生成客户端代码
                String clientCode = DeepSeekApiClient.generateClientCode(
                    Files.readString(file)
                );
                // 合并调用替换
                CodeMerger.replaceMethodCalls(file, TARGET_METHOD_CALL, clientCode);     
            }
        } catch (MethodNotFoundException e) {
            System.err.println("错误: " + e.getMessage());
            restoreBackup(findLatestBackup());
        } catch (Exception e) {
            System.err.println("重构失败: " + e.getMessage());
            e.printStackTrace();
            restoreBackup(findLatestBackup());
        }
    }

    private static List<Path> findMethodCallFiles(String projectPath, String methodName) throws IOException {
    return Files.walk(Paths.get(projectPath))
            .filter(p -> p.toString().endsWith(".java"))
            .filter(p -> containsMethodCall(p, methodName))
            .collect(Collectors.toList());
}

private static boolean containsMethodCall(Path filePath, String methodName) {
    try {
        CompilationUnit cu = StaticJavaParser.parse(filePath);
        return cu.findAll(MethodCallExpr.class).stream()
                .anyMatch(mce -> mce.getNameAsString().equals(methodName));
    } catch (Exception e) {
        return false;
    }}

    private static Path findTargetFile(String projectPath, String methodSignature) throws Exception {
        return Files.walk(Paths.get(projectPath))
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> containsMethod(p, methodSignature))
                .findFirst()
                .orElseThrow(() -> new MethodNotFoundException("未找到目标方法: " + methodSignature));
    }

    private static boolean containsMethod(Path filePath, String methodSignature) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(filePath);
            return cu.findAll(MethodDeclaration.class).stream()
                    .anyMatch(m -> m.getDeclarationAsString().equals(methodSignature));
        } catch (Exception e) {
            return false;
        }
    }

    private static void createBackup(Path file) throws IOException {
        Path backupPath = Paths.get(file + BACKUP_EXT);
        Files.copy(file, backupPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void validateSyntax(String code) {
        try {
            StaticJavaParser.parse(code);
        } catch (Exception e) {
            throw new RuntimeException("生成代码存在语法错误: " + e.getMessage());
        }
    }

    private static void restoreBackup(Path backupFile) {
        try {
            if (backupFile != null && Files.exists(backupFile)) {
                Path original = Paths.get(backupFile.toString().replace(BACKUP_EXT, ""));
                Files.move(backupFile, original, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("已恢复备份文件");
            }
        } catch (IOException ex) {
            System.err.println("备份恢复失败: " + ex.getMessage());
        }
    }

    private static Path findLatestBackup() {
        try {
            return Files.walk(Paths.get(PROJECT_ROOT))
                    .filter(p -> p.toString().endsWith(BACKUP_EXT))
                    .max(Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    static class MethodNotFoundException extends Exception {
        public MethodNotFoundException(String message) {
            super(message);
        }
    }
}