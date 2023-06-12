plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":bee_router:router_core"))
    implementation("com.google.auto.service:auto-service:1.1.0")
//    implementation("com.google.code.gson:gson:2.9.0")
    kapt("com.google.auto.service:auto-service:1.1.0")
}
