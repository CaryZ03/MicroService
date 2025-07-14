package com.micro.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
public class StaticNodeAnalyser {

//    public static void main(String[] args) throws Exception {

    @PostMapping("/staticNode")
    public List<String> staticNode(@RequestBody String targetProjectRoot) {

        // 目标项目的编译类文件目录
        String targetProjectClassesDir = targetProjectRoot + "/src/main/java";
        File targetProjectClasses = new File(targetProjectClassesDir);


        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(targetProjectClasses));

        File dependencyDir = new File(targetProjectRoot + "/fuxi-static-dependency");
        File tempDir = new File("temp-jars");
        if (!tempDir.exists()) tempDir.mkdir();

        for (File file : dependencyDir.listFiles((dir, name) -> name.endsWith(".jar"))) {
            try{
                File copied = new File(tempDir, file.getName());
                Files.copy(file.toPath(), copied.toPath(), StandardCopyOption.REPLACE_EXISTING);
                combinedTypeSolver.add(new JarTypeSolver(copied));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 配置 JavaParser
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        List<String> foundMethods = new ArrayList<>();

        List<File> javaFiles = listJavaFilesRecursively(targetProjectClasses);
        System.out.println("Found " + javaFiles.size() + " Java files");
        for (File javaFile : javaFiles) {
            System.out.println("Processing: " + javaFile);
            foundMethods.addAll(findMethods(parser, javaFile));
//            System.out.println(callGraph);
        }

        System.out.println("Methods:" + foundMethods);

        for (File f : tempDir.listFiles()) {
            f.delete();
        }
        tempDir.delete();

        return foundMethods;
    }

    private static List<File> listJavaFilesRecursively(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(listJavaFilesRecursively(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    private static List<String> findMethods(JavaParser parser, File javaFile) {

        List<String> foundMethods = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();

            // 遍历所有方法并添加 @Trace 注解
            for (TypeDeclaration<?> type : cu.getTypes()) {

                if (type.getAnnotations().stream().anyMatch(annotation -> {
                    String name = annotation.getNameAsString();
                    return name.equals("SpringBootApplication");
                })) {
                    continue;
                } else if (type.getAnnotations().stream().anyMatch(annotation -> {
                    String name = annotation.getNameAsString();
                    return name.equals("Entity") || name.equals("Repository") || name.equals("TableName");
                })) {
                    continue;
                } else if (type.getFullyQualifiedName().toString().contains(".model.") || type.getFullyQualifiedName().toString().contains(".entity.") || type.getFullyQualifiedName().toString().contains(".reqs.") || type.getFullyQualifiedName().toString().contains(".rsps.") || type.getFullyQualifiedName().toString().contains(".util.") || type.getFullyQualifiedName().toString().contains(".config.") || type.getFullyQualifiedName().toString().contains(".client.")) {
                    continue;
                }

                if (type instanceof ClassOrInterfaceDeclaration coi) {
                    if (coi.isInterface()) {
//                        excludedNames.add(type.getFullyQualifiedName().get());
                        continue;
                    }
                } else {
                    continue;
                }

                ResolvedReferenceTypeDeclaration resolved = coi.resolve();
                boolean isException = false;
                for (ResolvedReferenceType ancestor : resolved.getAllAncestors()) {
                    String ancestorName = ancestor.getQualifiedName();
                    if (ancestorName.equals("java.lang.Exception") || ancestorName.equals("java.lang.Throwable")) {
                        isException = true;
                        break;
                    }
                }
                if (isException) {
                    continue;
                }

                for (MethodDeclaration method : type.getMethods()) {
                    foundMethods.add(method.resolve().getQualifiedSignature());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundMethods;
    }

}