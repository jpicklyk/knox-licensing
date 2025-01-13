plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.convention.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "net.sfelabs.knox_tactical"

    defaultConfig {
        testInstrumentationRunner = "net.sfelabs.knox_tactical.TacticalJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java")
            java.srcDirs("build/generated/ksp/debug/kotlin")
        }
    }

    packaging {
        resources {
            excludes += arrayOf(
                "/META-INF/{LICENSE.md,LICENSE-notice.md}",
                "META-INF/gradle/incremental.annotation.processors"
            )
        }
    }
}

ksp {
    arg("android.namespace", android.namespace ?: "")
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module)
    implementation(libs.jetbrains.annotations)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
    implementation(project(":android-log-wrapper"))
    implementation(project(":core:common"))
    implementation(project(":core:usecase-executor"))
    implementation(project(":core:knox-android"))
    implementation(project(":core:knox-feature"))
    implementation(project(":core:knox-feature-ui"))
    implementation(project(":core:knox-feature-hilt"))
    implementation(project(":core:knox-feature-processor"))
    "ksp"(project(":core:knox-feature-processor"))
    implementation(libs.androidx.test.runner)
    implementation(libs.jetbrains.annotations)
    //The Tactical Knox SDK shall not be available outside this module (compileOnly)
    compileOnly(fileTree("libs/knoxsdk.jar"))
    androidTestCompileOnly(fileTree("libs/knoxsdk.jar"))

    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.kotlinx.coroutines.test)

}