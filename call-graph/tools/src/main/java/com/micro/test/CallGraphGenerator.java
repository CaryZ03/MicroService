package com.micro.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

@RestController
public class CallGraphGenerator {

//    public static void main(String[] args) throws Exception {

    @PostMapping("/callGraph")
    public Map<String, List<String>> callGraph(@RequestBody String targetProjectRoot) {
        // 项目根目录
//        String targetProjectRoot = "D:\\Programs\\MicroService\\call-graph\\tools\\demo";
        addSkyWalkingDependency(targetProjectRoot + "/pom.xml");
        File targetProjectDir = new File(targetProjectRoot);

        // 目标项目的编译类文件目录
        String targetProjectClassesDir = targetProjectRoot + "/src/main/java";
        File targetProjectClasses = new File(targetProjectClassesDir);

        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(targetProjectClasses));

        // 配置 JavaParser
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        JavaParserFacade facade = JavaParserFacade.get(combinedTypeSolver);

        Map<String, List<String>> callGraph = new HashMap<>();
        callGraph.computeIfAbsent("Excluded_Names", k -> new ArrayList<>());

        List<File> javaFiles = listJavaFilesRecursively(targetProjectClasses);
        System.out.println("Found " + javaFiles.size() + " Java files");
        for (File javaFile : javaFiles) {
            System.out.println(javaFile);
            List<String> excludedNames = addTraceAnnotation(parser, javaFile);
            if (excludedNames.isEmpty()) {
                getCallGraph(parser, facade, javaFile, callGraph);
            }
            callGraph.get("Excluded_Names").addAll(excludedNames);
//                System.out.println(callGraph);
        }
//        excludedNames.forEach(name -> System.out.println("Name: " + name));
//
//        callGraph.get("Excluded_Names").addAll(excludedNames);
        // 输出调用链
//        callGraph.forEach((caller, callees) -> System.out.println(caller + " -> " + callees));

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
        return callGraph;
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

    private static void getCallGraph(JavaParser parser, JavaParserFacade facade, File javaFile, Map<String, List<String>> callGraph) {
        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();
//            System.out.println(cu.findAll(MethodDeclaration.class));
            // 遍历所有方法并提取调用链
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                String methodName = method.resolve().getQualifiedSignature();
//                method.getSignature().asString();
//                System.out.println(method);
//                System.out.println(method.findAll(MethodCallExpr.class));
                callGraph.computeIfAbsent(methodName, k -> new ArrayList<>());
                for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                    callGraph.get(methodName).add(parseMethodName(call, facade));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseMethodName(Expression callExpr, JavaParserFacade facade) {
        if (callExpr instanceof MethodCallExpr call) {
            try {
                System.out.println(call);
                ResolvedMethodDeclaration resolvedCall = call.resolve();
                System.out.println(resolvedCall);
                String calledMethodName = resolvedCall.getQualifiedSignature();
                System.out.println(calledMethodName);
                return calledMethodName;
            } catch (Exception e) {
                Optional<Expression> optScope = call.getScope();
                if (optScope.isPresent()) {
                    Expression callClass = optScope.get();
                    System.out.println(callClass);
                    return parseMethodName(callClass, facade);
                }
                return null;
            }
        } else {
            ResolvedType resolvedCallClass = facade.getType(callExpr);
            System.out.println(resolvedCallClass.describe());
            return resolvedCallClass.describe();
        }
    }

    private static List<String> addTraceAnnotation(JavaParser parser, File javaFile) {

        List<String> excludedNames = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();

            boolean found = false;
            // 遍历所有方法并添加 @Trace 注解
            for (TypeDeclaration<?> type : cu.getTypes()) {
                if (type.getAnnotations().stream().anyMatch(annotation -> {
                    String name = annotation.getNameAsString();
                    return name.equals("Entity") || name.equals("Repository") || name.equals("SpringBootApplication");
                })) {
                    excludedNames.add(type.getFullyQualifiedName().get());
                    continue;
                }
                for (MethodDeclaration method : type.getMethods()) {
                    found = true;
                    if (!method.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("Trace"))) {
                        MarkerAnnotationExpr traceAnnotation = new MarkerAnnotationExpr();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excludedNames;
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