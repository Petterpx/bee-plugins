plugins {
    `kotlin-dsl`
}
val parentPath: String = rootDir.parentFile.parentFile.path
apply(from = "$parentPath/.buildscript/agp_common.gradle")
apply(from = "$parentPath/.buildscript/maven_plugin.gradle")
