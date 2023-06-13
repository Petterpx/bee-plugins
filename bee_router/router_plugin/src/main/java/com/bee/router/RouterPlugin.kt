package com.bee.router

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Bee Router Plugin
 * @author petterp
 */
class RouterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) return
        project.extensions.create(BEE_ROUTER_EXTENSION, RouterExtension::class.java)
        // 文档是通过读取mapping,class Node生成
        project.extensions.getByType(AndroidComponentsExtension::class.java).apply {
            onVariants {
                project.extensions.getByName(BEE_ROUTER_EXTENSION) as RouterExtension
                val taskProvider =
                    project.tasks.register<RouterTask>("${it.name}$BEE_ROUTER_TASK_NAME")
                it.artifacts.forScope(ScopedArtifacts.Scope.ALL).use(taskProvider)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        RouterTask::jars,
                        RouterTask::dirs,
                        RouterTask::output,
                    )
            }
        }
    }

    companion object {
        const val BEE_ROUTER_EXTENSION = "router"
        const val BEE_ROUTER_TASK_NAME = "BeeRouterTask"
        const val BEE_ROUTER_MAPPING_DIR = "bee_router/mapping"
        const val BEE_ROUTER_DOC_PATH = "bee_router/routerDoc.md"
    }
}
