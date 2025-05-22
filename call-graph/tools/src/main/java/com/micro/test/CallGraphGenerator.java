package com.micro.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.PrettyPrinter;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

        JavaParserFacade facade = JavaParserFacade.get(combinedTypeSolver);

        Map<String, List<String>> callGraph = new HashMap<>();
        callGraph.computeIfAbsent("Excluded_Names", k -> new ArrayList<>());

        Map<String, List<String>> impls = new HashMap<>();

        List<File> javaFiles = listJavaFilesRecursively(targetProjectClasses);
        System.out.println("Found " + javaFiles.size() + " Java files");
        for (File javaFile : javaFiles) {
            System.out.println("Processing: " + javaFile);
            List<String> excludedNames = addTraceAnnotation(parser, javaFile, impls);
            if (excludedNames.isEmpty()) {
                getCallGraph(parser, facade, javaFile, callGraph);
            }
            callGraph.get("Excluded_Names").addAll(excludedNames);
//            System.out.println(callGraph);
        }
        System.out.println("Impls: " + impls);
        System.out.println(callGraph.get("Excluded_Names"));

        System.out.println("CallGraph:" + callGraph);

        Map<String, List<String>> newCallGraph = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : callGraph.entrySet()) {

            if (entry.getKey().equals("Excluded_Names")) continue;
            String caller = replaceInterfaceWithImpl(entry.getKey(), impls);
            List<String> callees = new ArrayList<>();
            for (String callee : entry.getValue()) {
                callees.add(replaceInterfaceWithImpl(callee, impls));
            }
            newCallGraph.put(caller, callees);
        }
        callGraph = newCallGraph;


        for (File f : tempDir.listFiles()) {
            f.delete();
        }
        tempDir.delete();


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

    private static String replaceInterfaceWithImpl(String methodSignature, Map<String, List<String>> impls) {
        System.out.println(methodSignature);
        if (!methodSignature.contains("(")) return methodSignature;
        // 提取类全名，比如从 com.a.b.c() 提取 com.a.b
        int dotPos = methodSignature.substring(0, methodSignature.indexOf("(")).lastIndexOf(".");
//        System.out.println(dotPos);

        String className = methodSignature.substring(0, dotPos);
//        System.out.println(className);
        String methodName = methodSignature.substring(dotPos);
//        System.out.println(methodName);

        for (String interfaceName : impls.keySet()) {
            if (className.equals(interfaceName) && impls.get(interfaceName).size() == 1) {
                String implClass = impls.get(interfaceName).get(0);
                return implClass + methodName;
            }
        }

        return methodSignature; // 没有替换则返回原样
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
            System.out.println("Compilation Unit: " + cu);
            System.out.println("Methods: " + cu.findAll(MethodDeclaration.class));
            // 遍历所有方法并提取调用链
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                System.out.println(method);
                try{
                    String methodName = method.resolve().getQualifiedSignature();
                    System.out.println("Found Method: " + methodName);
    //                method.getSignature().asString();
    //                System.out.println(method);
    //                System.out.println(method.findAll(MethodCallExpr.class));
                    callGraph.computeIfAbsent(methodName, k -> new ArrayList<>());
                    for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                        callGraph.get(methodName).add(parseMethodName(call, facade));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private static String parseMethodName(Expression callExpr, JavaParserFacade facade) {
        if (callExpr instanceof MethodCallExpr call) {
            try {
                System.out.println("Found Call: " + call);
                ResolvedMethodDeclaration resolvedCall = call.resolve();
                System.out.println("Resolved Call: " + resolvedCall);
                String calledMethodName = resolvedCall.getQualifiedSignature();
                System.out.println("Callee Name: " + calledMethodName);
                return calledMethodName;
            } catch (Exception e) {
                e.printStackTrace();
                Optional<Expression> optScope = call.getScope();
                if (optScope.isPresent()) {
                    Expression callClass = optScope.get();
                    System.out.println("Resolved Callee Class: " + callClass);
                    return parseMethodName(callClass, facade);
                } else {
                    System.out.println(call);
                }
                return null;
            }
        } else {
            ResolvedType resolvedCallClass = facade.getType(callExpr);
            System.out.println("Resolved Callee Class Describe: " + resolvedCallClass.describe());
            return resolvedCallClass.describe();
        }
    }

    private static List<String> addTraceAnnotation(JavaParser parser, File javaFile, Map<String, List<String>> interfaceImpl) {

        List<String> excludedNames = new ArrayList<>();

        try (FileInputStream in = new FileInputStream(javaFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();

            boolean found = false;
            // 遍历所有方法并添加 @Trace 注解
            for (TypeDeclaration<?> type : cu.getTypes()) {

                if (type.getAnnotations().stream().anyMatch(annotation -> {
                    String name = annotation.getNameAsString();
                    return name.equals("SpringBootApplication");
                })) {
                    excludedNames.add("FUXI_FOUND_MAIN_CLASS_" + type.getFullyQualifiedName().orElse("<UnknownClass>"));
                    continue;
                } else if (type.getAnnotations().stream().anyMatch(annotation -> {
                    String name = annotation.getNameAsString();
                    return name.equals("Entity") || name.equals("Repository") || name.equals("TableName");
                })) {
                    excludedNames.add(type.getFullyQualifiedName().get());
                    continue;
                } else if (type.getFullyQualifiedName().toString().contains(".model.") || type.getFullyQualifiedName().toString().contains(".entity.") || type.getFullyQualifiedName().toString().contains(".reqs.") || type.getFullyQualifiedName().toString().contains(".rsps.") || type.getFullyQualifiedName().toString().contains(".util.")) {
                    excludedNames.add(type.getFullyQualifiedName().get());
                    continue;
                }

                if (type instanceof ClassOrInterfaceDeclaration coi) {
                    if (coi.isInterface()) {
//                        excludedNames.add(type.getFullyQualifiedName().get());
                        continue;
                    }
                } else {
                    excludedNames.add(type.getFullyQualifiedName().get());
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
                    excludedNames.add(type.getFullyQualifiedName().get());
                    continue;
                }

                List<ClassOrInterfaceType> implementedTypes = coi.getImplementedTypes();
                for (ClassOrInterfaceType impl : implementedTypes) {
                    String implName = impl.resolve().describe();
                    interfaceImpl.computeIfAbsent(implName, k -> new ArrayList<>());
                    interfaceImpl.get(implName).add(coi.getFullyQualifiedName().orElse(null));
//                    System.out.println("Implements: " + );
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