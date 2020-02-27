package com.lyc.appinject.visitors;

import com.lyc.appinject.data.Impl;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
public class InjectWriteClassVisitor extends ClassVisitor implements Opcodes {
    private final Map<String, Impl> singleClasses;
    private final Map<String, List<Impl>> oneToManyClasses;

    public InjectWriteClassVisitor(ClassVisitor cv, Map<String, Impl> singleClasses, Map<String, List<Impl>> oneToManyClasses) {
        super(ASM6, cv);
        this.singleClasses = singleClasses;
        this.oneToManyClasses = oneToManyClasses;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("initSingleApiMap".equals(name)) {
            return new InitSingleApiMapMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), singleClasses);
        } else if ("initOneToManyApiMap".equals(name)) {
            return new InitOneToManyApiMapVisitor(super.visitMethod(access, name, desc, signature, exceptions), oneToManyClasses, access, name, desc);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
