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
        project.extensions.getByType(AndroidComponentsExtension::class.java).apply {
            onVariants {
                variants.add(String.format("transform%sClassesWithAsm", it.name.capitalize()))
                it.instrumentation.apply {
                    transformClassesWith(
                        AnalysisTransForm::class.java,
                        InstrumentationScope.PROJECT,
                    ) {}
                    setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
                }
            }
        }

        project.afterEvaluate {
            val extension = (project.properties[ANALYSIS_EXTENSION] as RuleExtension)
            MethodAnalysisUtils.initConfig(extension)
            if (variants.isEmpty()) return@afterEvaluate
            tasks.firstOrNull { task ->
                variants.any {
                    task.name.contains(it)
                }
            }?.doLast {
                MethodAnalysisUtils.end()
            }
        }
    }

    companion object {
        private const val ANALYSIS_EXTENSION = "analysis"
        private const val TASK_NAME = "analysisTask"
    }
}
