package com.micro.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class CallGraphGenerator {
    private static final Map<String, String> callGraph = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // 初始化 JavaSymbolSolver
        JavaSymbolSolver solver = new JavaSymbolSolver(new ReflectionTypeSolver());
        JavaParser.getStaticConfiguration().setSymbolResolver(solver);

        // 读取 Java 源代码文件
        FileInputStream in = new FileInputStream("src/main/java/com/example/javaparser/Example.java");
        CompilationUnit cu = JavaParser.parse(in);

        // 遍历所有方法并提取调用链
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            String methodName = method.getNameAsString();
            for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                ResolvedMethodDeclaration resolvedCall = call.resolve();
                String calledMethodName = resolvedCall.getName();
                callGraph.putIfAbsent(methodName, calledMethodName);
            }
        }

        // 输出调用链
        callGraph.forEach((caller, callee) -> System.out.println(caller + " -> " + callee));
    }
}