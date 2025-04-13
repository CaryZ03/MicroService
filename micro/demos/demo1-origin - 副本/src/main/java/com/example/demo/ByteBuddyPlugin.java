//package com.example.demo;
//
//import net.bytebuddy.ByteBuddy;
//import net.bytebuddy.description.annotation.AnnotationDescription;
//import net.bytebuddy.matcher.ElementMatchers;
//import org.apache.skywalking.apm.toolkit.trace.Trace;
//
//import java.lang.instrument.Instrumentation;
//import java.lang.instrument.ClassFileTransformer;
//import java.security.ProtectionDomain;
//
//import sun.misc.Unsafe;
//
//public class ByteBuddyTraceAgent {
//    public static void premain(String agentArgs, Instrumentation inst) {
//        inst.addTransformer(new ClassFileTransformer() {
//            @Override
//            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
//                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
//                try {
//                    Unsafe unsafe = Unsafe.getUnsafe();
//                    Class<?> clazz = unsafe.defineClass(className.replace('/', '.'), classfileBuffer, 0, classfileBuffer.length, null, null);
//
//                    return new ByteBuddy()
//                            .redefine(clazz)
//                            .visit(new AsmVisitorWrapper.ForDeclaredMethods(
//                                    ElementMatchers.any(),
//                                    new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper() {
//                                        @Override
//                                        public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription methodDescription, MethodVisitor methodVisitor) {
//                                            return new MethodVisitor(Opcodes.ASM9, methodVisitor) {
//                                                @Override
//                                                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
//                                                    if (descriptor.equals(Type.getDescriptor(Trace.class))) {
//                                                        return new AnnotationVisitor(Opcodes.ASM9, super.visitAnnotation(descriptor, visible)) {
//                                                            @Override
//                                                            public void visit(String name, Object value) {
//                                                                if ("operationName".equals(name)) {
//                                                                    value = className + "::" + methodDescription.getName();
//                                                                }
//                                                                super.visit(name, value);
//                                                            }
//                                                        };
//                                                    }
//                                                    return super.visitAnnotation(descriptor, visible);
//                                                }
//                                            };
//                                        }
//                                    }
//                            ))
//                            .make()
//                            .getBytes();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return classfileBuffer; // 如果出错，返回原始字节码
//                }
//            }
//        });
//    }
//}