plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.bee.router")
    id("kotlin-kapt")
}

beeRouter {
    enableDoc = true
}

android {
    namespace = "com.bee.router_simple"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.bee.router_simple"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    kapt {
        useBuildCache = true
        arguments {
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(mapOf("path" to ":bee_router:simple_login")))

    implementation(project(":bee_router:router_core"))
    "kapt"(project(":bee_router:router_apt"))
}
