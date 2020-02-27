package com.lyc.appinject

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class InjectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "================================"
        println "Apply Inject Plugin"
        AppExtension android = project.extensions.getByType(AppExtension.class)
        if (android == null) {
            throw RuntimeErrorException("Android Extension == null! Cannot Apply Plugin!")
        }
        android.registerTransform(new InjectTransform(project))
        println "================================"
    }
}
