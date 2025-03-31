import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import codegen.CodeMerger;
import codegen.DeepSeekApiClient;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    // 配置参数
    private static final String PROJECT_ROOT = "./testProject";
    private static final String TARGET_METHOD = "add";
    private static final String TARGET_METHOD_SIGN = "public int add(int a, int b, int c)";
    private static final String BACKUP_EXT = ".bak";

    public static void main(String[] args) {
        try {
            // 0. 检查环境
            if (!DeepSeekApiClient.isNetworkAvailable()) {
                System.err.println("网络不可用，请检查连接");
                return;
            } else {
                System.out.println("网络已连接");
            }
            if (!Files.exists(Paths.get(PROJECT_ROOT))) {
                System.err.println("项目路径不存在: " + PROJECT_ROOT);
                return;
            }

            // 1. 查找目标方法所在文件
            Path targetFile = findTargetFile(PROJECT_ROOT, TARGET_METHOD_SIGN);
            System.out.println("找到目标方法位于: " + targetFile);

            // 2. 创建文件备份
            createBackup(targetFile);
            System.out.println("已创建备份文件: " + targetFile + BACKUP_EXT);

            // 3. 读取原始代码
            String originalCode = Files.readString(targetFile);

            // 4. 生成API代码
            String generatedCode = DeepSeekApiClient.generateServiceCode(TARGET_METHOD,
                    targetFile, originalCode);
            System.out.println("API代码生成成功\n" + generatedCode);

            // 5. 合并代码
            String cleanedCode = CodeMerger.writeController(originalCode, generatedCode,
                    PROJECT_ROOT);

            // 6. 验证语法
            validateSyntax(cleanedCode);

            // 7. 输出结果
            System.out.println("API代码已生成到指定包路径");

            // 8. 查找并替换所有方法调用
            MethodSignature signature = parseMethodSignature(TARGET_METHOD_SIGN);
            List<Path> targetFiles = findMethodCallFiles(PROJECT_ROOT, signature.methodName);

            for (Path file : targetFiles) {
                CodeMerger.replaceMethodCalls(
                        file,
                        signature.methodName,
                        signature.paramCount,
                        signature.paramTypes,
                        signature.returnType,
                        signature.paramNames);
            }

            // 9. 测试其他方法调用
            MethodSignature _signature = parseMethodSignature("public int add(int a, int b)");
            List<Path> _targetFiles = findMethodCallFiles(PROJECT_ROOT, signature.methodName);

            for (Path file : _targetFiles) {
                CodeMerger.replaceMethodCalls(
                        file,
                        _signature.methodName,
                        _signature.paramCount,
                        _signature.paramTypes,
                        _signature.returnType,
                        _signature.paramNames);
            }

        } catch (Exception e) {
            System.err.println("重构失败: " + e.getMessage());
            e.printStackTrace();
            restoreBackup(findLatestBackup());
        }
    }

    /*
     * 查找包含指定方法调用的文件
     * 
     * @param projectPath 项目路径
     * 
     * @param methodName 方法名
     * 
     * @return 符合条件的文件列表
     */
    private static List<Path> findMethodCallFiles(String projectPath, String methodName) throws IOException {
        return Files.walk(Paths.get(projectPath))
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> containsMethodCall(p, methodName))
                .collect(Collectors.toList());
    }

    /*
     * 解析方法签名
     * 
     * @param signature 方法签名
     * 
     * @return 方法签名对象
     */
    private static MethodSignature parseMethodSignature(String signature) {
        Pattern pattern = Pattern.compile(
                "^\\s*(?:public|protected|private|static)?\\s+" +
                        "(\\S+)\\s+" + // 返回类型
                        "(\\w+)\\s*" + // 方法名
                        "\\(([^)]*)\\)" + // 参数列表
                        "\\s*$");

        Matcher matcher = pattern.matcher(signature);
        if (matcher.find()) {
            String returnType = matcher.group(1);
            String methodName = matcher.group(2);
            List<String> params = Arrays.stream(matcher.group(3).split(","))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .collect(Collectors.toList());

            // 提取参数类型和名称
            List<String> paramTypes = new ArrayList<>();
            List<String> paramNames = new ArrayList<>();
            for (String param : params) {
                String[] parts = param.split("\\s+");
                if (parts.length < 2) {
                    throw new IllegalArgumentException("参数格式错误: " + param);
                }
                paramTypes.add(parts[0]);
                paramNames.add(parts[1]);
            }

            return new MethodSignature(
                    returnType,
                    methodName,
                    params.size(),
                    paramTypes,
                    paramNames);
        }
        throw new IllegalArgumentException("无效的方法签名: " + signature);
    }

    /*
     * 检查文件是否包含指定方法调用
     * 
     * @param filePath 文件路径
     * 
     * @param methodName 方法名
     * 
     * @return 是否包含
     */
    private static boolean containsMethodCall(Path filePath, String methodName) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(filePath);
            return cu.findAll(MethodCallExpr.class).stream()
                    .anyMatch(mce -> mce.getNameAsString().equals(methodName));
        } catch (Exception e) {
            return false;
        }
    }

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

class MethodSignature {
    String returnType;
    String methodName;
    int paramCount;
    List<String> paramTypes; // 新增参数类型列表
    List<String> paramNames;

    public MethodSignature(String returnType, String methodName, int paramCount,
            List<String> paramTypes, List<String> paramNames) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.paramCount = paramCount;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
    }
}