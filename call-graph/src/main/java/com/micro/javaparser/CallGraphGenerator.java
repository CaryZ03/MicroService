package com.micro.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallGraphGenerator {
    private static final Map<String, List<String>> callGraph = new HashMap<>();

    public static void main(String[] args) throws Exception {
        // 项目根目录
        String targetProjectRoot = "D:\\Programs\\MicroService\\micro\\demos\\demo1-origin";
        addSkyWalkingDependency(targetProjectRoot + "\\pom.xml");
        File targetProjectDir = new File(targetProjectRoot);

        // 目标项目的编译类文件目录
//        String targetProjectClassesDir = targetProjectRoot + "\\target\\classes";
        String targetProjectClassesDir = targetProjectRoot + "\\src\\main\\java";
        File targetProjectClasses = new File(targetProjectClassesDir);

        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
//        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(targetProjectClasses));

        // 配置 JavaParser
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        // 递归解析项目中的所有 Java 文件
        if (targetProjectDir.exists() && targetProjectDir.isDirectory()) {
            List<File> javaFiles = listJavaFilesRecursively(targetProjectDir);
            System.out.println("Found " + javaFiles.size() + " Java files");
            for (File javaFile : javaFiles) {
                System.out.println(javaFile);
                addTraceAnnotation(parser, javaFile);
//                getCallGraph(parser, javaFile);
//                System.out.println(callGraph);
            }
        }
        // 输出调用链
//        callGraph.forEach((caller, callees) -> System.out.println(caller + " -> " + callees));
//
//        // 使用 Gson 将 Map 转换为 JSON 字符串
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(callGraph);
//
//        // 将 JSON 字符串写入文件
//        try (FileWriter writer = new FileWriter("callGraph.json")) {
//            writer.write(json);
//            System.out.println("Map has been saved to callGraph.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    private static void getCallGraph(JavaParser parser, File javaFile) throws Exception {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();
//            System.out.println(cu.findAll(MethodDeclaration.class));
            // 遍历所有方法并提取调用链
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                String methodName = method.resolve().getQualifiedName();
//                method.getSignature().asString();
                System.out.println(method);
//                System.out.println(method.findAll(MethodCallExpr.class));
                callGraph.computeIfAbsent(methodName, k -> new ArrayList<>());
                for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                    try {
                        System.out.println(call);
                        ResolvedMethodDeclaration resolvedCall = call.resolve();
                        System.out.println(resolvedCall);
                        String calledMethodName = resolvedCall.getQualifiedName();
                        callGraph.get(methodName).add(calledMethodName);
                    } catch (Exception e) {
                        // 忽略无法解析的调用
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    private static void addTraceAnnotation(JavaParser parser, File javaFile) throws IOException {
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

    public static void addSkyWalkingDependency(String pomPath) {
        File pomFile = new File(pomPath);
        try {
            // 读取 pom.xml 文件
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomFile));

            // 检查是否已经有相同的依赖
            boolean dependencyExists = model.getDependencies().stream()
                    .anyMatch(d -> "org.apache.skywalking".equals(d.getGroupId()) &&
                            "apm-toolkit-trace".equals(d.getArtifactId()));

            if (!dependencyExists) {
                // 添加新的依赖
                Dependency dependency = new Dependency();
                dependency.setGroupId("org.apache.skywalking");
                dependency.setArtifactId("apm-toolkit-trace");
                dependency.setVersion("9.3.0");
                model.addDependency(dependency);

                // Step 3: 写入 StringWriter
                StringWriter stringWriter = new StringWriter();
                MavenXpp3Writer xpp3Writer = new MavenXpp3Writer();
                xpp3Writer.write(stringWriter, model);
                String rawXml = stringWriter.toString();

                // Step 4: 解析成 DOM
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                factory.setIgnoringElementContentWhitespace(true); // 关键！
                Document document = factory.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(rawXml.getBytes("UTF-8")));

                // Step 5: 使用 Transformer 控制缩进写出
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new FileWriter(pomFile));
                transformer.transform(source, result);

                System.out.println("Dependency added successfully.");
            } else {
                System.out.println("Dependency already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}