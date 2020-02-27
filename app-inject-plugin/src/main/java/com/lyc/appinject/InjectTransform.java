package com.lyc.appinject;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.lyc.appinject.data.Impl;
import com.lyc.appinject.visitors.InjectCollectorClassVisitor;
import com.lyc.appinject.visitors.InjectWriteClassVisitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by Liu Yuchuan on 2020/1/12.
 */
public class InjectTransform extends Transform {

    private static final String MODULE_HOLDERS_CLASS_NAME = "com/lyc/appinject/AppInjectHolders.class";
    private final Project project;
    private final Set<String> singleApiClasses = new HashSet<>();
    private final Set<String> oneToManyApiClasses = new HashSet<>();
    private final Map<String, List<Impl>> apiImplClasses = new HashMap<>();

    InjectTransform(Project project) {
        this.project = project;
    }

    private static boolean acceptClassFileName(String name) {
        if (name == null || !name.endsWith(".class")) {
            return false;
        }

        if (name.startsWith("androidx/") || name.startsWith("java/") || name.startsWith("android/") || name.startsWith("kotlin/") || name.startsWith("org/intellij/") || name.startsWith("org/jetbrains/") || name.startsWith("kotlinx/")) {
            return false;
        }

        int index = name.lastIndexOf("/");
        if (index != -1) {
            name = name.substring(index + 1);
        }

        // 过滤R.class
        if ("R.class".equals(name)) {
            return false;
        }

        return !"BuildConfig.class".equals(name);
    }

    @Override
    public String getName() {
        return "AppInject";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_WITH_IR_FOR_DEXING;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        System.out.println("===================== Inject Plugin Transform started =====================");
        final long startTime = System.currentTimeMillis();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        outputProvider.deleteAll();

        JarInput appInjectHoldersJar = null;

        for (TransformInput input : inputs) {
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                collectInfoFromDirectoryInput(directoryInput, transformInvocation.getOutputProvider());
            }

            for (JarInput jarInput : input.getJarInputs()) {
                if (collectInfoFromJarInput(jarInput, transformInvocation.getOutputProvider())) {
                    appInjectHoldersJar = jarInput;
                }
            }
        }

        if (appInjectHoldersJar == null) {
            throw new RuntimeException("Cannot find " + MODULE_HOLDERS_CLASS_NAME + "! Please check your proguard or if it's on your external libraries.");
        }

        checkCollectedInfo();

        if (!apiImplClasses.isEmpty()) {
            findAppInjectHoldersAndWrite(appInjectHoldersJar, transformInvocation.getOutputProvider());
        } else {
            System.out.println("No need to write to API, just copy jar...");
            final File dest = outputProvider.getContentLocation(appInjectHoldersJar.getFile().getAbsolutePath(), appInjectHoldersJar.getContentTypes(), appInjectHoldersJar.getScopes(), Format.JAR);
            FileUtils.copyFile(appInjectHoldersJar.getFile(), dest);
        }

        System.out.println("Inject Plugin cost " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("===================== Inject Plugin Transform finished =====================");
    }

    private void findAppInjectHoldersAndWrite(JarInput jarInput, TransformOutputProvider outputProvider) throws IOException {

        File tmpFile = new File(project.getBuildDir(), "tmp.jar");
        try {
            if (jarInput == null) {
                return;
            }

            final File file = jarInput.getFile();

            if (!file.getName().endsWith(".jar")) {
                return;
            }

            JarFile jarFile = new JarFile(file);
            Enumeration enumeration = jarFile.entries();
            if (tmpFile.exists() && !tmpFile.delete()) {
                throw new RuntimeException("Cannot delete file " + tmpFile);
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile));
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                if (MODULE_HOLDERS_CLASS_NAME.equals(entryName)) {

                    Map<String, Impl> singleApiMap = new HashMap<>();
                    Map<String, List<Impl>> oneToManyApiMap = new HashMap<>();

                    for (String singleApiClass : singleApiClasses) {
                        List<Impl> impls = apiImplClasses.get(singleApiClass);
                        if (impls != null) {
                            singleApiMap.put(singleApiClass, impls.get(0));
                        }
                    }
                    for (String oneToManyClass : oneToManyApiClasses) {
                        List<Impl> impls = apiImplClasses.get(oneToManyClass);
                        if (impls != null) {
                            oneToManyApiMap.put(oneToManyClass, impls);
                        }
                    }

                    StringBuilder outputSb = new StringBuilder("============== Starts to write to AppInjectHolders ==============")
                            .append("\n\n")
                            .append("***********************************************************\n")
                            .append("************** begin SingleApiImplClassesMap **************\n")
                            .append("***********************************************************\n");
                    singleApiMap.forEach((singleApiClass, impl) -> {
                        outputSb.append(singleApiClass).append(" -> ").append(impl).append("\n");
                    });
                    outputSb.append("*********************************************************\n")
                            .append("************** end SingleApiImplClassesMap **************\n")
                            .append("*********************************************************\n\n\n");


                    outputSb.append("******************************************************************\n")
                            .append("**************** begin OneToManyApiImplClassesMap ****************\n")
                            .append("******************************************************************\n");
                    oneToManyApiMap.forEach((oneToManyClass, impls) -> {
                        outputSb.append(oneToManyClass).append(":\n");
                        for (Impl impl : impls) {
                            outputSb.append("-> ").append(impl).append("\n");
                        }
                    });
                    outputSb.append("****************************************************************\n")
                            .append("**************** end OneToManyApiImplClassesMap ****************\n")
                            .append("****************************************************************\n\n\n");

                    System.out.println(outputSb.toString());

                    jarOutputStream.putNextEntry(zipEntry);
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream));
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                    InjectWriteClassVisitor cv = new InjectWriteClassVisitor(classWriter, singleApiMap, oneToManyApiMap);
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES);
                    byte[] code = classWriter.toByteArray();
                    jarOutputStream.write(code);

                    System.out.println("============== Finish writing to AppInjectHolders ==============\n");

                } else {
                    jarOutputStream.putNextEntry(zipEntry);
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            jarFile.close();

            // No modify to bytecode
            File dest = outputProvider.getContentLocation(file.getAbsolutePath(),
                    jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
            FileUtils.copyFile(tmpFile, dest);
        } finally {
            if (tmpFile.exists() && !tmpFile.delete()) {
                tmpFile.deleteOnExit();
            }
        }
    }

    private void checkCollectedInfo() {

        HashSet<String> intersections = new HashSet<>(singleApiClasses);
        intersections.retainAll(oneToManyApiClasses);
        if (!intersections.isEmpty()) {
            throw new RuntimeException("Intersections of singleApiClasses And oneToManyApiClasses is not empty!");
        }

        // check if serviceImp has its super interface
        apiImplClasses.forEach((key, value) -> {
            if (!singleApiClasses.contains(key) && !oneToManyApiClasses.contains(key)) {
                throw new RuntimeException(key + " does not implement any interface with @InjectApi!");
            }
        });


        for (String singleApiClass : singleApiClasses) {
            if (!apiImplClasses.containsKey(singleApiClass)) {
                System.err.println("InjectApi " + singleApiClass + " does not have any implement class.");
            } else {
                if (apiImplClasses.get(singleApiClass).size() > 1) {
                    throw new RuntimeException("@InjectApi " + singleApiClass + " has more than one impls? Please set param oneToMany=true or remove other impls form project!");
                }
            }
        }

        for (String singleApiClass : oneToManyApiClasses) {
            if (!apiImplClasses.containsKey(singleApiClass)) {
                System.err.println("InjectApi " + singleApiClass + " does not have any implement class.");
            }
        }
    }

    private void collectInfoFromDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) throws IOException {
        if (directoryInput == null) {
            System.err.println("directoryInput == null");
            return;
        }

        final File dir = directoryInput.getFile();
        if (!dir.isDirectory()) {
            System.err.println("directoryInput.getFile is not directory!");
            return;
        }

        Iterator<File> iterator = FileUtils.iterateFiles(dir, new String[]{"class"}, true);
        iterator.forEachRemaining(file -> {
            if (file.isFile()) {
                try {
                    collectInfoFromClass(file.getName(), FileUtils.readFileToByteArray(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        File dest = outputProvider.getContentLocation(dir.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(),
                Format.DIRECTORY);
        FileUtils.copyDirectory(directoryInput.getFile(), dest);
    }

    /**
     * @param jarInput       jar input file
     * @param outputProvider from TransformInvocation
     * @return if this jarInput is where ApiInjectHolders locates
     */
    private boolean collectInfoFromJarInput(JarInput jarInput, TransformOutputProvider outputProvider) throws IOException {
        if (jarInput == null) {
            System.err.println("jarInput == null");
            return false;
        }

        final File file = jarInput.getFile();

        if (!file.getName().endsWith(".jar")) {
            System.err.println("!jarFile.name.endsWith(\".jar\")");
            return false;
        }

        boolean result = false;
        JarFile jarFile = new JarFile(jarInput.getFile());
        Enumeration enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (MODULE_HOLDERS_CLASS_NAME.equals(entryName)) {
                // 找到了需要改字节码的类，稍后返回true
                // 并且这个类不需要查看
                result = true;
            } else if (acceptClassFileName(entryName)) {
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                collectInfoFromClass(entryName, IOUtils.toByteArray(inputStream));
            }
        }
        jarFile.close();

        if (!result) {
            // 需要修改字节码的类会在稍后处理
            // 这里用absolutePath作为唯一标识
            final File dest = outputProvider.getContentLocation(jarInput.getFile().getAbsolutePath(),
                    jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
            FileUtils.copyFile(jarInput.getFile(), dest);
        }

        return result;
    }

    private void collectInfoFromClass(String name, byte[] classBytes) {
        if (acceptClassFileName(name)) {
            ClassReader classReader = new ClassReader(classBytes);
            InjectCollectorClassVisitor cv = new InjectCollectorClassVisitor(singleApiClasses, oneToManyApiClasses, apiImplClasses);
            classReader.accept(cv, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
    }
}
