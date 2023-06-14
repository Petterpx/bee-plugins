plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}
apply(from = "${rootProject.rootDir}/.buildscript/maven_java.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
