package com.lyc.appinject.visitors;

import com.lyc.appinject.data.Impl;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
class InitOneToManyApiMapVisitor extends AdviceAdapter implements Opcodes {
    private final Map<String, List<Impl>> oneToManyApiMap;

    InitOneToManyApiMapVisitor(MethodVisitor mv, Map<String, List<Impl>> oneToManyApiMap, int access, String name, String desc) {
        super(ASM6, mv, access, name, desc);
        this.oneToManyApiMap = oneToManyApiMap;
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        oneToManyApiMap.forEach((superClass, imps) -> {
            if (!imps.isEmpty()) {
                System.out.println("Start put oneToManyApi: " + superClass);
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/lyc/appinject/ModuleApiHolders", "oneToManyApiClassMap", "Ljava/util/Map;");
                mv.visitLdcInsn(Type.getType("L" + superClass + ";"));
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/util/List");
                mv.visitVarInsn(ASTORE, 1);
                mv.visitVarInsn(ALOAD, 1);
                Label addListPos = new Label();
                mv.visitJumpInsn(IFNONNULL, addListPos);
                mv.visitTypeInsn(NEW, "java/util/ArrayList");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
                mv.visitVarInsn(ASTORE, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/lyc/appinject/ModuleApiHolders", "oneToManyApiClassMap", "Ljava/util/Map;");
                mv.visitLdcInsn(Type.getType("L" + superClass + ";"));
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitInsn(POP);
                mv.visitLabel(addListPos);
                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/util/List"}, 0, null);
                for (Impl imp : imps) {
                    System.out.println("-> put" + imp.className + ";");
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitLdcInsn(Type.getType("L" + imp.className + ";"));
                    mv.visitLdcInsn(imp.createMethod);
                    mv.visitMethodInsn(INVOKESTATIC, "com/lyc/appinject/ImplementationFactory", "createImpl", "(Ljava/lang/Class;Ljava/lang/String;)Lcom/lyc/appinject/impl/Implementation;", false);
                    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                    mv.visitInsn(POP);
                }
                System.out.println("Finish put oneToManyApi" + superClass);
            }
        });
    }
}
