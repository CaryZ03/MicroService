package com.micro.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TraceAnnotationGenerator {
    public static void main(String[] args) throws Exception {
        // 项目根目录
        String projectRoot = "src/main/java/com/micro/javaparser/demo";
        File projectDir = new File(projectRoot);

        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(projectDir));

        // 配置 JavaParser
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        // 递归解析项目中的所有 Java 文件
        if (projectDir.exists() && projectDir.isDirectory()) {
            File[] javaFiles = projectDir.listFiles();
            System.out.println("Found " + javaFiles.length + " Java files");
            if (javaFiles != null) {
                for (File javaFile : javaFiles) {
                    processJavaFile(parser, javaFile);
                }
            }
        }
    }

    private static void processJavaFile(JavaParser parser, File javaFile) throws IOException {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();

            boolean found = false;
            // 遍历所有方法并添加 @Trace 注解
            for (TypeDeclaration<?> type : cu.getTypes()) {
                for (MethodDeclaration method : type.getMethods()) {
                    found = true;
                    if (!method.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("Trace"))) {
                        NormalAnnotationExpr traceAnnotation = new NormalAnnotationExpr();
                        traceAnnotation.setName("Trace");
                        method.addAnnotation(traceAnnotation);
                    }
                }
            }

            if (found) {
                // 添加 import 语句
                if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals("org.apache.skywalking.apm.toolkit.trace.Trace"))) {
                    cu.addImport("org.apache.skywalking.apm.toolkit.trace.Trace");
                }
            }

            // 保存修改后的文件
            String modifiedCode = new PrettyPrinter().print(cu);
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(modifiedCode);
            }
        }
    }
}