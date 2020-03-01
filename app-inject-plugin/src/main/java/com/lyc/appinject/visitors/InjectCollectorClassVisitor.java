package com.lyc.appinject.visitors;


import com.lyc.appinject.data.Impl;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
public class InjectCollectorClassVisitor extends ClassVisitor implements Opcodes {

    private static final String INJECT_API_DESC = "Lcom/lyc/appinject/annotations/InjectApi;";
    private static final String INJECT_API_IMPL_DESC = "Lcom/lyc/appinject/annotations/InjectApiImpl;";
    private static final String IMPL_PARAM = "api";
    private final Set<String> singleApiClasses;
    private final Set<String> oneToManyInjectApiClasses;
    private final Map<String, List<Impl>> ImplClassesMap;

    private final Map<String, String> typeCheckMap;
    private final Map<String, Set<String>> parentMap;

    private static final String CREATE_METHOD_DESC = "Lcom/lyc/appinject/CreateMethod;";
    private static final String CREATE_METHOD_PARAM = "createMethod";

    private static final String ONE_TO_MANY_PARAM = "oneToMany";

    private String currentSuperName;
    private String currentName;
    private String currentCreateMethod;
    private String[] currentInterfaces;
    private String superNameFromClass;
    private boolean isAbstractClass;
    private boolean isInterface;

    public InjectCollectorClassVisitor(Set<String> singleApiClasses, Set<String> oneToManyInjectApiClasses, Map<String, List<Impl>> ImplClassesMap, Map<String, String> typeCheckMap, Map<String, Set<String>> parentMap) {
        super(ASM6);
        this.singleApiClasses = singleApiClasses;
        this.oneToManyInjectApiClasses = oneToManyInjectApiClasses;
        this.ImplClassesMap = ImplClassesMap;
        this.typeCheckMap = typeCheckMap;
        this.parentMap = parentMap;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        currentName = name;
        superNameFromClass = superName;
        currentInterfaces = interfaces;
        isAbstractClass = (access & ACC_ABSTRACT) != 0;
        isInterface = (access & ACC_INTERFACE) != 0;
        Set<String> set = new HashSet<>();
        if (superName != null && !"java/lang/Object".equals(superName)) {
            set.add(superName);
        }
        if (interfaces != null) {
            set.addAll(Arrays.asList(interfaces));
        }
        parentMap.put(name, set);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitAttribute(Attribute attr) {

        super.visitAttribute(attr);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (INJECT_API_DESC.equals(desc)) {
            if (!isInterface && !isAbstractClass) {
                throw new RuntimeException("Only interface or abstract class can be annotated with @InjectApi");
            }
            return new InjectApiAnnotationVisitor(currentName);
        } else if (INJECT_API_IMPL_DESC.equals(desc)) {
            if (isAbstractClass) {
                throw new RuntimeException("Abstract class " + currentName + " cannot be annotated with @InjectApiImpl!");
            }
            return new InjectApiImplAnnotationVisitor(currentName);
        }
        return super.visitAnnotation(desc, visible);
    }

    private class InjectApiAnnotationVisitor extends AnnotationVisitor {
        final String currentName;
        // default value is false
        private boolean oneToMany = false;

        InjectApiAnnotationVisitor(String currentName) {
            super(ASM6);
            this.currentName = currentName;
        }

        @Override
        public void visit(String name, Object value) {
            if (ONE_TO_MANY_PARAM.equals(name)) {
                oneToMany = Boolean.parseBoolean(String.valueOf(value));
            }
            super.visit(name, value);
        }

        @Override
        public void visitEnd() {
            if (oneToMany) {
                oneToManyInjectApiClasses.add(currentName);
            } else {
                singleApiClasses.add(currentName);
            }
            System.out.println("Find a @InjectApi: " + currentName + ", oneToMany=" + oneToMany);
            super.visitEnd();
        }
    }


    private class InjectApiImplAnnotationVisitor extends AnnotationVisitor {
        final String currentName;

        InjectApiImplAnnotationVisitor(String currentName) {
            super(ASM6);
            this.currentName = currentName;
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            if (CREATE_METHOD_PARAM.equals(name) && CREATE_METHOD_DESC.equals(desc)) {
                currentCreateMethod = value;
            }
            super.visitEnum(name, desc, value);
        }

        @Override
        public void visit(String name, Object value) {
            if (IMPL_PARAM.equals(name)) {
                String desc = String.valueOf(value);
                if (desc.length() > 2) {
                    // remove "L" and ";" from method desc
                    String superName = desc.substring(1, desc.length() - 1);

                    System.out.println("Super of " + currentName + " is " + superNameFromClass);
                    if (((superNameFromClass == null) || ("java/lang/Object").equals(superNameFromClass)) &&
                            (currentInterfaces == null || currentInterfaces.length == 0)) {
                        throw new RuntimeException(currentName + " never implements or extends " + superName + "!");
                    }

                    boolean isImpl = false;
                    if (currentInterfaces != null) {
                        for (String currentInterface : currentInterfaces) {
                            if (superName.equals(currentInterface)) {
                                isImpl = true;
                                break;
                            }
                        }
                    }

                    if (!isImpl) {
                        if (superName.equals(superNameFromClass)) {
                            isImpl = true;
                        }
                    }

                    if (!isImpl) {
//                        String msg = "Impl " + currentName + " does not explicit implement(extend) " + superName + "!";
//                        System.err.println("WARNING: " + msg + " Which may lead to runtime error when cast " + currentName + " to " + superName);
//                        throw new RuntimeException(msg);
                        typeCheckMap.put(currentName, superName);
                    }
                    currentSuperName = superName;
                } else {
                    throw new RuntimeException("Cannot recognize implement: Name=" + currentName + ", extension param=" + value);
                }
            }
            super.visit(name, value);
        }

        @Override
        public void visitEnd() {
//            if (currentSuperName != null) {
            List<Impl> list = ImplClassesMap.getOrDefault(currentSuperName, new ArrayList<>());
            list.add(new Impl(currentName, currentCreateMethod));
            ImplClassesMap.put(currentSuperName, list);
            System.out.println("Find a valid @InjectApiImpL: super=" + currentSuperName + ", imp=" + currentName);
            currentSuperName = null;
//            }
            super.visitEnd();
        }
    }
}
