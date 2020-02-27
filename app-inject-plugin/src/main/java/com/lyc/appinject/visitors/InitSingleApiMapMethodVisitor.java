package com.lyc.appinject.visitors;

import com.lyc.appinject.data.Impl;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
class InitSingleApiMapMethodVisitor extends MethodVisitor implements Opcodes {
    private final Map<String, Impl> singleApiMap;

    InitSingleApiMapMethodVisitor(MethodVisitor mv, Map<String, Impl> singleApiMap) {
        super(ASM6, mv);
        this.singleApiMap = singleApiMap;
    }

    @Override
    public void visitCode() {
        singleApiMap.forEach((superClass, impl) -> {
            System.out.println("Put singleApi: " + superClass + " -> " + impl);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lyc/appinject/AppInjectHolders", "singleApiClassMap", "Ljava/util/Map;");
            mv.visitLdcInsn(Type.getType("L" + superClass + ";"));
            mv.visitLdcInsn(Type.getType("L" + impl.className + ";"));
            mv.visitLdcInsn(impl.createMethod);
            mv.visitMethodInsn(INVOKESTATIC, "com/lyc/appinject/ImplementationFactory", "createImpl", "(Ljava/lang/Class;Ljava/lang/String;)Lcom/lyc/appinject/impl/Implementation;", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
        });
        super.visitCode();
    }
}
