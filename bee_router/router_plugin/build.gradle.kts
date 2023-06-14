plugins {
    `kotlin-dsl`
}
val rootPath: String = rootDir.parentFile.parentFile.path
apply(from = "$rootPath/.buildscript/agp_common.gradle")
apply(from = "$rootPath/.buildscript/maven_plugin.gradle")
