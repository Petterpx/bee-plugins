package com.bee.router

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
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
            // 遍历文件夹 (复制与收集)
            dirs.get().forEach { directory ->
                val directoryPath =
                    if (directory.asFile.absolutePath.endsWith(File.separatorChar)) {
                        directory.asFile.absolutePath
                    } else {
                        directory.asFile.absolutePath + File.separatorChar
                    }
                println("----dir----> handling: $directoryPath")
                directory.asFile.walk().asSequence().filter { it.isFile }.forEach dirChild@{ file ->
                    val entryName = if (leftSlash) {
                        file.path.substringAfter(directoryPath)
                    } else {
                        file.path.substringAfter(directoryPath).replace(File.separatorChar, '/')
                    }
                    if (entryName.isEmpty()) return@dirChild
                    // TODO: 找到之后将其复制到自定义的目录，收集起来
                    if (entryName.startsWith(BEE_ROUTER_MAPPING)) {
                        println("----找到了-[for dirs]->entryName: $entryName")
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
                    println("----jar----> handling: ${jar.name}")
                    jar.entries().iterator().asSequence().filter {
                        !it.isDirectory && it.name.isNotEmpty() && !it.name.contains("META-INF/")
                    }.forEach jarFile@{ entry ->
                        // TODO: 在这里可以对jar进行自己的处理，收集起来
                        // 找到我们指定的注入类时，将其保存下来，便于后续注入
                        if (entry.name == BEE_ROUTER_NAVIGATION) {
                            jar.getInputStream(entry).use {
                                injectByteArray = it.readAllBytes()
                            }
                            println("----已找到控制器->entryName: ${entry.name}")
                            return@jarFile
                        }
                        if (entry.name.startsWith(BEE_ROUTER_MAPPING)) {
                            println("----找到了-[for jars]->entryName: ${entry.name}")
                            // 进行自己的操作
                        }
                        // copy to jar
                        jar.getInputStream(entry).use {
                            jarOutput.saveEntry(entry.name, it)
                        }
                    }
                }
            }
            println("BeeRouter plugin query mapping spend ${System.currentTimeMillis() - startTime} ms")
            println("--bee-> 开始进行代码插入------>")
//            checkNotNull(injectByteArray) {
//                println("请确定你依赖了bee_router")
//            }
//            val hackBytes = RouterInject.hackMethod(injectByteArray!!, mappingList)
//            jarOutput.saveEntry(BEE_ROUTER_CONTROL, ByteArrayInputStream(hackBytes))
            println("--bee-> 代码插入成功------>")
        }
        println("BeeRouter plugin inject time spend ${System.currentTimeMillis() - startTime} ms")
    }

    private fun JarOutputStream.saveEntry(entryName: String, inputStream: InputStream) {
        putNextEntry(JarEntry(entryName))
        inputStream.copyTo(this)
        closeEntry()
    }

    companion object {
        const val BEE_ROUTER_MAPPING = "com/bee/router/mapping/RouterMapping_"
        const val BEE_ROUTER_NAVIGATION = "com/bee/router/core/RouterNavigation.class"
    }
}
