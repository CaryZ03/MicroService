package com.micro.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class ASTAnalyzer {

    public static void main(String[] args) {
        String projectRoot = "D:\\Programs\\MicroService\\micro\\demos\\demo1-origin";
        projectRoot = projectRoot + "\\src\\main\\java";
        List<File> fileList = new ArrayList<>();
        File projectDir = new File(projectRoot);
        JavaParser parser = new JavaParser();
        parseDirectory(projectDir, fileList);
        for (File file : fileList) {
            String filename = Paths.get(projectRoot).relativize(Paths.get(file.getAbsolutePath())).toString();
            try (FileInputStream in = new FileInputStream(file)) {
                CompilationUnit cu = parser.parse(in).getResult().orElseThrow();
                StaticParseNode root = analyzeCompilationUnit(cu);
                root.setFileName(filename);
                List<StaticParseNode> classes = new ArrayList<>();
                for (ClassOrInterfaceDeclaration ClassOrInterface : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                    StaticParseNode classNode = analyzeClass(ClassOrInterface);
                    classNode.setFileName(filename);
                    classes.add(classNode);
                    List<StaticParseNode> methods = new ArrayList<>();
                    for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                        StaticParseNode methodNode = analyzeMethod(method);
                        methodNode.setFileName(filename);
                        methods.add(methodNode);
                    }
                    classNode.setChildren(methods);
                }
                root.setChildren(classes);
                outputJson(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void outputJson(StaticParseNode root) {
        // 使用 GsonBuilder 设置格式化输出
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(root);

        String filename = root.getFileName();
        filename = filename.split(".java")[0].replace("\\", "&");

        // 输出到文本文件
        try (FileWriter writer = new FileWriter(filename + ".json")) {
            writer.write(json);
            System.out.println("JSON written in " + filename + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void parseDirectory(File directory, List<File> fileList) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                parseDirectory(file, fileList);
            }
        } else if (directory.getName().endsWith(".java")) {
            fileList.add(directory);
        }
    }

    public static StaticParseNode analyzeCompilationUnit(CompilationUnit cu) {
        StaticParseNode root = new StaticParseNode();

        root.setFuncName("root");
        root.setNodeType("root");
//        root.setNode(cu.toString());
        root.setNode("\n\ndef __init__():\n    pass\n");

        Optional<Range> ndoeRange = cu.getRange();
        if (ndoeRange.isPresent()) {
            root.setLineno(ndoeRange.get().begin.line);
            root.setEndLineno(ndoeRange.get().end.line);
        }

        // 提取导入语句
        root.setImports(cu.getImports().stream()
                .map(NodeWithName::getNameAsString)
                .collect(Collectors.toList()));

        // 提取全局变量（假设全局变量为顶级的字段声明）
        List<String> globalVariables = new ArrayList<>();
        cu.getTypes().forEach(type -> {
            if (type instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) type;
                classDeclaration.getFields().forEach(field -> {
                    field.getVariables().forEach(variable -> {
                        globalVariables.add(variable.getNameAsString());
                    });
                });
            }
        });
        root.setGlobalVariables(globalVariables);

        // 提取方法的注释
        cu.getComment().ifPresent(comment -> {
            if (comment instanceof JavadocComment) {
                root.setDocstring(((JavadocComment) comment).getContent());
            }
        });

        return root;
    }

    public static StaticParseNode analyzeClass(ClassOrInterfaceDeclaration classDeclaration) {
        StaticParseNode classNode = new StaticParseNode();
        classNode.setFuncName(classDeclaration.getNameAsString());
        classNode.setNodeType("class");
//        classNode.setNode(classDeclaration.toString());
        classNode.setNode("\n\ndef __init__():\n    pass\n");

        Optional<Range> ndoeRange = classDeclaration.getRange();
        if (ndoeRange.isPresent()) {
            classNode.setLineno(ndoeRange.get().begin.line);
            classNode.setEndLineno(ndoeRange.get().end.line);
        }

        // 提取字段
        List<String> defVariables = new ArrayList<>();
        classDeclaration.getFields().forEach(field -> {
            field.getVariables().forEach(variable -> {
                defVariables.add(variable.getNameAsString());
            });
        });
        classNode.setDefVariables(defVariables);

        // 提取字段初始化中的 use_variables
        List<String> useVariables = new ArrayList<>();

        classDeclaration.getFields().forEach(field -> {
            field.getVariables().forEach(variable -> {
                variable.getInitializer().ifPresent(initExpr -> {
                    initExpr.findAll(NameExpr.class).forEach(nameExpr -> {
                        useVariables.add(nameExpr.getNameAsString());
                    });
                });
            });
        });

        // 提取初始化代码块中的 use_variables
        classDeclaration.getMembers().forEach(member -> {
            if (member.isInitializerDeclaration()) {
                member.asInitializerDeclaration().getBody().findAll(NameExpr.class).forEach(nameExpr -> {
                    useVariables.add(nameExpr.getNameAsString());
                });
            }
        });
        classNode.setUseVariables(useVariables);

        // 提取方法的注释
        classDeclaration.getComment().ifPresent(comment -> {
            if (comment instanceof JavadocComment) {
                classNode.setDocstring(((JavadocComment) comment).getContent());
            }
        });

        return classNode;
    }

    public static StaticParseNode analyzeMethod(MethodDeclaration methodDeclaration) {
        StaticParseNode methodNode = new StaticParseNode();

        methodNode.setFuncName(methodDeclaration.getNameAsString());
        methodNode.setNodeType("function");
//        methodNode.setNode(methodDeclaration.toString());
        methodNode.setNode("\n\ndef __init__():\n    pass\n");

        Optional<Range> ndoeRange = methodDeclaration.getRange();
        if (ndoeRange.isPresent()) {
            methodNode.setLineno(ndoeRange.get().begin.line);
            methodNode.setEndLineno(ndoeRange.get().end.line);
        }

        // 提取方法的参数
        List<String> defVariables = new ArrayList<>();
        methodDeclaration.getParameters().forEach(param -> {
            defVariables.add(param.getNameAsString());
        });

        // 提取方法体中的变量
        List<String> useVariables = new ArrayList<>();
        if (methodDeclaration.getBody().isPresent()) {
            BlockStmt body = methodDeclaration.getBody().get();
            body.getStatements().forEach(statement -> {
                if (statement instanceof ExpressionStmt expressionStmt) {
                    Expression expression = expressionStmt.getExpression();
                    if (expression instanceof VariableDeclarationExpr varDecl) {
                        varDecl.getVariables().forEach(variable -> {
                            defVariables.add(variable.getNameAsString());
                        });
                    } else if (expression instanceof NameExpr nameExpr) {
                        useVariables.add(nameExpr.getNameAsString());
                    }
                }
            });
        }
        methodNode.setDefVariables(defVariables);
        methodNode.setUseVariables(useVariables);

        // 提取方法的返回变量
        List<String> returnVariables = new ArrayList<>();
        methodDeclaration.findAll(ReturnStmt.class).forEach(returnStmt -> {
            if (returnStmt.getExpression().isPresent()) {
                returnVariables.add(returnStmt.getExpression().get().toString());
            }
        });
        methodNode.setReturnVariables(returnVariables);

        // 提取方法的注释
        methodDeclaration.getComment().ifPresent(comment -> {
            if (comment instanceof JavadocComment) {
                methodNode.setDocstring(((JavadocComment) comment).getContent());
            }
        });

        return methodNode;
    }
}