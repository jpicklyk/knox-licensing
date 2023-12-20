plugins {
    id("sfelabs.android.feature")
}

android {
    namespace = "net.sfelabs.knox_tactical"

    defaultConfig {
        testInstrumentationRunner = "net.sfelabs.knox_tactical.TacticalJUnitRunner"
    }
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module)
    implementation(project(":android-log-wrapper"))
    //The Tactical Knox SDK shall not be available outside this module (compileOnly)
    compileOnly(fileTree("libs/knoxsdk.jar"))
    androidTestCompileOnly(fileTree("libs/knoxsdk.jar"))
    implementation(libs.androidx.test.runner)
}