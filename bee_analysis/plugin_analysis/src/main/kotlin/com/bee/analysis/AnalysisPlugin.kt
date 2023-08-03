package com.bee.analysis

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 代码分析Plugin
 * @author petterp
 */
class AnalysisPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) return
        project.extensions.create(ANALYSIS_EXTENSION, RuleExtension::class.java)
        project.task(TASK_NAME)

        val variants = mutableListOf<String>()
        val androidCom = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidCom.onVariants { variant ->
            val name = variant.name
            variants.add(name)
            variant.instrumentation.apply {
                transformClassesWith(
                    AnalysisTransForm::class.java,
                    InstrumentationScope.PROJECT,
                ) { params ->
                    params.buildType.set(name)
                }
                setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
            }
        }
        project.afterEvaluate {
            if (variants.isEmpty()) return@afterEvaluate
            val extension = (project.properties[ANALYSIS_EXTENSION] as RuleExtension)
            MethodAnalysisUtils.initConfig(extension)
            variants.forEach { type ->
                val taskName = String.format("transform%sClassesWithAsm", type.capitalize())
                tasks.getByName(taskName).doLast {
                    MethodAnalysisUtils.end(type)
                }
            }
        }
    }

    companion object {
        private const val ANALYSIS_EXTENSION = "analysis"
        private const val TASK_NAME = "analysisTask"
    }
}
