package com.bee.router

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges

/**
 * 路由DocTask
 * @author petterp
 */
abstract class RouterDocTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val mappingFile: RegularFileProperty

    @get:OutputFile
    abstract val outPutDocFile: RegularFileProperty

    @TaskAction
    fun action(inputs: InputChanges) {
        val docFile = outPutDocFile.get().asFile
        val mps = mappingFile.get().asFile
        if (!inputs.isIncremental) docFile.delete()
        if (!mps.exists()) return
        val listFiles = mps.listFiles()
        if (listFiles.isNullOrEmpty()) return
        val mdBuilder = StringBuilder("# 页面文档\n\n")
        listFiles.filter { childFile ->
            childFile.name.endsWith(".json")
        }.forEach { file ->
            val jsonSlurper = JsonSlurper()
            (jsonSlurper.parse(file) as? ArrayList<Map<String, String>>)?.forEach { map ->
                val url = map["url"]
                val description = map["description"]
                val realPath = map["realPath"]
                mdBuilder.append("\n## $description\n")
                mdBuilder.append("- url=$url\n")
                mdBuilder.append("- realPath=$realPath\n")
            }
        }
        docFile.writeText(mdBuilder.toString())
    }
}
