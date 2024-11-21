plugins {
    id("sfelabs.android.feature")
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
}


dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module)
    implementation(project(":android-log-wrapper"))
    implementation(project(":core:common"))
    implementation(project(":core:knox-api"))
    implementation(project(":core:knox-feature"))
    implementation(project(":core:knoxfeature-processor"))
    ksp(project(":core:knoxfeature-processor"))
    //The Tactical Knox SDK shall not be available outside this module (compileOnly)
    compileOnly(fileTree("libs/knoxsdk.jar"))
    androidTestCompileOnly(fileTree("libs/knoxsdk.jar"))
    implementation(libs.androidx.test.runner)
}