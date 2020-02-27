package com.lyc.appinject;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by Liu Yuchuan on 2020/2/27.
 */
public class InjectPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("================================");
        System.out.println("Apply Inject Plugin");
        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        if (android == null) {
            throw new RuntimeException("Android Extension == null! Cannot Apply Plugin!");
        }
        android.registerTransform(new InjectTransform(project));
        System.out.println("================================");
    }
}
