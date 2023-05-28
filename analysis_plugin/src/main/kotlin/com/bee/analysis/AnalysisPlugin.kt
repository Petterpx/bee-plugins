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
        project.extensions.create(ANALYSIS_EXTENSION, RuleExtension::class.java)
        project.task(TASK_NAME)

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)
            // 基于variant可实现不同变种的处理逻辑
            androidComponents.onVariants {
                it.instrumentation.apply {
                    transformClassesWith(
                        AnalysisTransForm::class.java,
                        InstrumentationScope.PROJECT,
                    ) {}
                    setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
                }
            }
        }
    }

    companion object {
        private const val ANALYSIS_EXTENSION = "analysis"
        private const val TASK_NAME = "analysisTask"
    }
}
