package com.bee.router

import com.bee.router.utils.RouterInject
import com.bee.router.utils.DocUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 *
 * @author petterp
 */
abstract class RouterTask : DefaultTask() {

    @get:InputFiles
    abstract val jars: ListProperty<RegularFile>

//    @set:Input
//    abstract var enableDoc: Property<String>
//    abstract var docPath:Property<String>

    @get:InputFiles
    abstract val dirs: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val startTime = System.currentTimeMillis()
        val leftSlash = File.separator == "/"
        // 输入的 jar、aar、源码
        JarOutputStream(output.asFile.get().outputStream()).use { jarOutput ->
            var injectByteArray: ByteArray? = null
            val mappingList = mutableListOf<String>()
            val docList = mutableListOf<String>()
            // 遍历文件夹 (复制与收集)
            dirs.get().forEach { directory ->
                val directoryPath =
                    if (directory.asFile.absolutePath.endsWith(File.separatorChar)) {
                        directory.asFile.absolutePath
                    } else {
                        directory.asFile.absolutePath + File.separatorChar
                    }
                directory.asFile.walk().asSequence().filter { it.isFile }.forEach dirChild@{ file ->
                    val entryName = if (leftSlash) {
                        file.path.substringAfter(directoryPath)
                    } else {
                        file.path.substringAfter(directoryPath).replace(File.separatorChar, '/')
                    }
                    if (entryName.isEmpty()) return@dirChild
                    if (entryName.startsWith(BEE_ROUTER_MAPPING)) {
                        mappingList.add(entryName)
                        DocUtils.scanInput(file.inputStream(), docList)
                    }
                    // copy to
                    file.inputStream().use { input ->
                        jarOutput.saveEntry(entryName, input)
                    }
                }
            }

            // 遍历jar (复制与收集)，其实这里就是其他modules
            jars.get().forEach { file ->
                JarFile(file.asFile).use { jar ->
                    jar.entries().iterator().asSequence().filter {
                        !it.isDirectory && it.name.isNotEmpty() && !it.name.contains("META-INF/")
                    }.forEach jarFile@{ entry ->
                        // 找到我们指定的注入类时，将其保存下来，便于后续注入
                        if (entry.name == BEE_ROUTER_NAVIGATION) {
                            jar.getInputStream(entry).use {
                                injectByteArray = it.readAllBytes()
                            }
                            return@jarFile
                        }
                        if (entry.name.startsWith(BEE_ROUTER_MAPPING)) {
                            mappingList.add(entry.name)
                            DocUtils.scanInput(jar.getInputStream(entry), docList)
                        }
                        // copy to jar
                        jar.getInputStream(entry).use {
                            jarOutput.saveEntry(entry.name, it)
                        }
                    }
                }
            }

            println("BeeRouter plugin query mapping spend ${System.currentTimeMillis() - startTime} ms")
            checkNotNull(injectByteArray) {
                println("Make sure you rely on router_core modules?")
            }
            println("BeeRouter-> start router mapping inject------>")
            val navigationBytes = RouterInject.inject(injectByteArray!!, mappingList)
            jarOutput.saveEntry(BEE_ROUTER_NAVIGATION, ByteArrayInputStream(navigationBytes))

            val builder = StringBuilder("[")
            docList.forEach {
                builder.append(it).append(",")
            }
            builder.append("]")
            val file = File(project.buildDir, "beeRouter.json")
            file.bufferedWriter().use {
                it.write(builder.toString())
            }
            println("BeeRouter-> router mapping inject success------>")
        }
        println("BeeRouter plugin all spend ${System.currentTimeMillis() - startTime} ms")
    }

    private fun JarOutputStream.saveEntry(entryName: String, inputStream: InputStream) {
        putNextEntry(JarEntry(entryName))
        inputStream.copyTo(this)
        closeEntry()
    }

    companion object {
        const val BEE_ROUTER_MAPPING = "com/bee/router/mapping/_RouterMapping_"
        const val BEE_ROUTER_NAVIGATION = "com/bee/router/core/RouterNavigation.class"
    }
}
