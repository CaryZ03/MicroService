package com.micro.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
public class SpringCloudReconstructor {

//    public static void main(String[] args) {
//        SpringCloudReconstructor scr = new SpringCloudReconstructor();
//        scr.reconstruct();
//    }

    @PostMapping("/reconstruct")
    public ServiceTemp reconstruct(@RequestBody ServiceTemp req) {
        String target_path = req.getTarget_path();
        Map<String, Integer> partition = req.getPartition();
        String port = req.getPort();
        List<String> functions = req.getFunctions();
        List<String> ins = req.getIns();
        List<String> outs = req.getOuts();
        System.out.println("Target Path: " + target_path);
        System.out.println("Functions: " + functions);
        System.out.println("Ins: " + ins);
        System.out.println("Outs: " + outs);

        File projectDir = new File(target_path);
        String rootPath = target_path + "/src/main/java";
        File rootDir = new File(rootPath);

        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(rootDir));

        // 配置 JavaParser
        JavaParser parser = new JavaParser();
        parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));

        String mainDir = addPom(target_path + "/pom.xml");
        parseUtils(rootPath, mainDir);
        parseConfig(target_path, port);

        System.out.println("Parsing Outs...");
        for (String path : outs) {
            if (!path.startsWith("com")) continue;
            System.out.println("path: " + path);
            Integer serviceId = partition.get(path);
            parseOut(parser, rootDir, path, serviceId, mainDir);

        }


        return req;
    }

    public void parseConfig(String targetPath, String port) {
        String configFilePath = targetPath + "/src/main/resources/application.properties";
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            // 如果 application.properties 文件不存在，改为 application.yml 文件
            configFilePath = targetPath + "/src/main/resources/application.yml";
        }

        try (InputStream input = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(input);

            // 修改 server.port 的值
            properties.setProperty("server.port", port);

            // 将修改后的属性写回文件
            try (OutputStream output = new FileOutputStream(configFile)) {
                properties.store(output, null);
            }

            System.out.println("server.port 已修改为: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseUtils(String rootDir, String mainDir) {
        String dir = rootDir + "/" + mainDir;
        List<String> file_paths = new ArrayList<>();
        file_paths.add(dir + "/RequestMapper.java");
        file_paths.add(dir + "/RequestWrapper.java");
        file_paths.add(dir + "/RestTemplateConfig.java");

        for (String file_path : file_paths) {
            try {
                File file = new File(file_path);
                CompilationUnit cu = StaticJavaParser.parse(file);

                // 2. 修改 package 声明
                cu.setPackageDeclaration(mainDir.replace("/", "."));

                // 3. 保存回文件（可覆盖原文件，也可另存）
                Files.write(Path.of(file_path), cu.toString().getBytes());

//                String modifiedCode = new PrettyPrinter().print(cu);
//                try (FileWriter writer = new FileWriter(targetFile)) {
//                    writer.write(modifiedCode);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void parseOut(JavaParser parser, File root, String path, Integer serviceId, String mainDir) {

        String methodPath = path.substring(0, path.indexOf('('));
        String classPath = methodPath.substring(0, methodPath.lastIndexOf('.'));
        String methodName = methodPath.substring(methodPath.lastIndexOf(".") + 1);
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String[] parameters = path.substring(path.indexOf('(') + 1, path.indexOf(')')).split(",");
        classPath = classPath.replace('.', File.separatorChar);
        String targetURL = "http://127.0.0.1:185" + String.format("%02d", serviceId);

        System.out.println(className + methodName);

        // 构造目标文件路径
        File targetFile = new File(root, classPath + ".java");

        System.out.println(targetFile.getAbsolutePath());
        try (FileInputStream in = new FileInputStream(targetFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();
            ClassOrInterfaceDeclaration classNode = cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(cls -> cls.getNameAsString().equals(className))
                    .findFirst()
                    .orElse(null);
            System.out.println("Found Class: " + classNode.resolve().getClassName());
            MethodDeclaration methodNode = classNode.getMethods().stream()
                    .filter(method -> method.resolve().getQualifiedSignature().equals(path))
                    .findFirst()
                    .orElse(null);
            System.out.println("Found Method: " + methodNode.resolve().getQualifiedSignature());


            if (methodNode.getBody().isPresent()) {
                methodNode.getBody().get().getStatements().clear();
            }

            if (!classNode.getFields().stream()
                    .flatMap(fd -> fd.getVariables().stream())
                    .anyMatch(vd -> vd.getType().toString().equals("RestTemplate") && vd.getNameAsString().equals("restTemplate"))) {
                FieldDeclaration restTemplateField = new FieldDeclaration();
                restTemplateField.setModifiers(Modifier.Keyword.PRIVATE);
                restTemplateField.addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, "RestTemplate"), "restTemplate"));
                restTemplateField.addAnnotation(new MarkerAnnotationExpr("Autowired"));

                // 获取类的成员列表
                NodeList<BodyDeclaration<?>> members = classNode.getMembers();
                members.add(0, restTemplateField);
                classNode.setMembers(members);

                // 添加 import 语句
                if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals("org.springframework.web.client.RestTemplate"))) {
                    cu.addImport("org.springframework.web.client.RestTemplate");
                }
                if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals("org.springframework.beans.factory.annotation.Autowired"))) {
                    cu.addImport("org.springframework.beans.factory.annotation.Autowired");
                }
            }


//            ExpressionStmt serviceProviderAddressStmt = new ExpressionStmt(
//                    new VariableDeclarationExpr(
//                            new VariableDeclarator(
//                                    new PrimitiveType(PrimitiveType.Primitive.STRING),
//                                    "SERVICE_PROVIDER_ADDRESS",
//                                    new StringLiteralExpr("http://127.0.0.1:18555")
//                            )
//                    )
//            );
//            methodNode.getBody().get().addStatement(0, serviceProviderAddressStmt);



            // 添加 import 语句
            addImport(cu, "java.util.ArrayList");
            addImport(cu, "java.util.List");

            // 添加 List<Object> params = new ArrayList<>();
            ClassOrInterfaceType listType = new ClassOrInterfaceType(null, new SimpleName("List"), new NodeList<>(new ClassOrInterfaceType(null, "Object")));
            VariableDeclarator paramsVarDecl = new VariableDeclarator(
                    listType,
                    "params",
                    new ObjectCreationExpr(null, new ClassOrInterfaceType(null, "ArrayList"), new NodeList<>())
            );
            // 创建变量声明表达式
            VariableDeclarationExpr paramsDeclExpr = new VariableDeclarationExpr(
                    new NodeList<>(paramsVarDecl) // 变量声明器列表
            );
            methodNode.getBody().get().addStatement(0, paramsDeclExpr);

            // 添加 params.add(user);
            methodNode.getParameters().forEach(param -> {
                ExpressionStmt addStmt = new ExpressionStmt(
                        new MethodCallExpr(
                                new NameExpr("params"),
                                "add",
                                new NodeList<>(new NameExpr(param.getNameAsString()))
                        )
                );
                methodNode.getBody().get().addStatement(addStmt);
            });


            // 添加 RequestWrapper wrapper = new RequestWrapper("com.micro.test.demo.ControllerB.test(com.micro.test.demo.User)", params);
            addImport(cu, mainDir.replace("/", ".") + ".RequestWrapper");

            VariableDeclarator wrapperVarDecl = new VariableDeclarator(
                    new ClassOrInterfaceType(null, "RequestWrapper"),
                    "wrapper",
                    new ObjectCreationExpr(
                            null,
                            new ClassOrInterfaceType(null, "RequestWrapper"),
                            new NodeList<>(
                                    new StringLiteralExpr(path),
                                    new NameExpr("params")
                            )
                    )
            );
            VariableDeclarationExpr wrapperDeclExpr = new VariableDeclarationExpr(
                    new NodeList<>(wrapperVarDecl) // 变量声明器列表
            );
            methodNode.getBody().get().addStatement(wrapperDeclExpr);


            // 添加 return restTemplate.postForObject(SERVICE_PROVIDER_ADDRESS + "/mapper", wrapper, String.class);
            ReturnStmt returnStmt = new ReturnStmt(
                    new MethodCallExpr(
                            new NameExpr("restTemplate"),
                            "postForObject",
                            new NodeList<>(
//                                    new BinaryExpr(new NameExpr("SERVICE_PROVIDER_ADDRESS"), new StringLiteralExpr("/mapper"), BinaryExpr.Operator.PLUS),
                                    new StringLiteralExpr(targetURL + "/mapper"),
                                    new NameExpr("wrapper"),
                                    new ClassExpr(methodNode.getType())
                            )
                    )
            );
            methodNode.getBody().get().addStatement(returnStmt);


            String modifiedCode = new PrettyPrinter().print(cu);
            try (FileWriter writer = new FileWriter(targetFile)) {
                writer.write(modifiedCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addImport(CompilationUnit cu, String stmt) {
        if (!cu.getImports().stream().anyMatch(i -> i.getNameAsString().equals(stmt))) {
            cu.addImport(stmt);
        }
    }


    public String addPom(String pomPath) {
        File pomFile = new File(pomPath);
        String mainDir = null;
        try {
            // 读取 pom.xml 文件
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomFile));

            mainDir = model.getGroupId() + "." + model.getArtifactId();
            mainDir = mainDir.replace(".", "/");

            // 检查是否已经有相同的依赖
            boolean dependencyExists = model.getDependencies().stream()
                    .anyMatch(d -> "com.google.code.gson".equals(d.getGroupId()) &&
                            "gson".equals(d.getArtifactId()));

            if (!dependencyExists) {
                // 添加新的依赖
                Dependency dependency = new Dependency();
                dependency.setGroupId("com.google.code.gson");
                dependency.setArtifactId("gson");
                dependency.setVersion("2.8.9");
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
        return mainDir;
    }

    public void parseIn(JavaParser parser, File root, String path) {

        String methodPath = path.substring(0, path.indexOf('('));
        String classPath = methodPath.substring(0, methodPath.lastIndexOf('.'));
        String methodName = methodPath.substring(methodPath.lastIndexOf(".") + 1);
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String[] parameters = path.substring(path.indexOf('(') + 1, path.indexOf(')')).split(",");
        classPath = classPath.replace('.', File.separatorChar);

        System.out.println(className + methodName);

        // 构造目标文件路径
        File targetFile = new File(root, classPath + ".java");

        System.out.println(targetFile.getAbsolutePath());
        try (FileInputStream in = new FileInputStream(targetFile)) {
            CompilationUnit cu = parser.parse(in).getResult().orElseThrow();
            ClassOrInterfaceDeclaration classNode = cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(cls -> cls.getNameAsString().equals(className))
                    .findFirst()
                    .orElse(null);
//            for (MethodDeclaration methodNode : classNode.getMethods()) {
////                System.out.println(methodNode.resolve().getQualifiedName());
//                System.out.println(methodNode.resolve().getQualifiedSignature());
//                System.out.println(path);
//            }
            MethodDeclaration methodNode = classNode.getMethods().stream()
                    .filter(method -> method.resolve().getQualifiedSignature().equals(path))
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//
//    public ClassExpr generateRestTemplateCall(Type returnType) {
//        String className;
//        switch (returnType) {
//            case VoidType voidType ->
//                // void 类型，不需要返回值
//                    throw new IllegalArgumentException("Void methods are not supported: " + returnType);
//            case ClassOrInterfaceType classType ->
//                // 类或接口类型
//                    className = classType.getNameAsString();
//            case PrimitiveType primitiveType ->
//                // 基本类型，使用对应的包装类
//                    className = getWrapperClassName(primitiveType);
//            case ArrayType arrayType ->
//                // 数组类型
//                    className = getArrayClassName(arrayType);
//            case TypeParameter typeParameter ->
//                // 泛型类型
//                    className = getGenericClassName(typeParameter);
//            case null, default ->
//                // 未知类型
//                    throw new IllegalArgumentException("Unsupported return type: " + returnType);
//        }
//        return new ClassExpr(new ClassOrInterfaceType(null, className));
//    }
//
//
//    private static String getWrapperClassName(PrimitiveType primitiveType) {
//        return switch (primitiveType.getType()) {
//            case BOOLEAN -> "Boolean";
//            case BYTE -> "Byte";
//            case SHORT -> "Short";
//            case INT -> "Integer";
//            case LONG -> "Long";
//            case FLOAT -> "Float";
//            case DOUBLE -> "Double";
//            case CHAR -> "Character";
//            default -> throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType.getType());
//        };
//    }
//
//    private static String getArrayClassName(ArrayType arrayType) {
//        Type componentType = arrayType.getComponentType();
//        if (componentType instanceof ClassOrInterfaceType) {
//            ClassOrInterfaceType classType = (ClassOrInterfaceType) componentType;
//            return classType.getNameAsString() + "[]";
//        } else if (componentType instanceof PrimitiveType) {
//            PrimitiveType primitiveType = (PrimitiveType) componentType;
//            return getWrapperClassName(primitiveType) + "[]";
//        } else {
//            throw new IllegalArgumentException("Unsupported array component type: " + componentType);
//        }
//    }
//
//    private static String getGenericClassName(TypeParameter typeParameter) {
//        // 这里需要根据具体的泛型类型进行处理，可能需要更复杂的逻辑
//        // 例如，可以使用 ParameterizedTypeReference
//        return "java.util.List<java.lang.String>"; // 示例
//    }

}