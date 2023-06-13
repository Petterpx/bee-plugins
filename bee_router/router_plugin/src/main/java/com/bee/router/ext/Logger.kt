package com.bee.router.ext

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Logger {
    private var gradleLogger: Logger? = null
    private var tag: String = ""
    fun init(project: Project, tag: String) {
        gradleLogger = project.logger
        com.bee.router.ext.Logger.tag = tag
    }

    fun i(info: String) {
        gradleLogger?.info("$tag >>> $info")
    }

    fun e(error: String) {
        gradleLogger?.info("$tag >>> $error")
    }

    fun w(warning: String) {
        gradleLogger?.info("$tag >>> $warning")
    }
}
