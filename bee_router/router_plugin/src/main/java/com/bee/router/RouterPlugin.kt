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
        project.extensions.getByType(AndroidComponentsExtension::class.java).apply {
            onVariants {
                val taskProvider = project.tasks.register<RouterTask>("${it.name}BeeRouterTask")
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
        const val BEE_ROUTER_MAPPING_DIR = "bee_router/mapping"
        const val BEE_ROUTER_DOC_PATH = "bee_router/routerDoc.md"
    }
}
