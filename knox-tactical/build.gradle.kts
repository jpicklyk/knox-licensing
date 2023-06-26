
plugins {
    id("sfelabs.android.feature")
}

android {
    namespace = "net.sfelabs.knox_tactical"
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(project(":android-log-wrapper"))
    //The Tactical Knox SDK shall not be available outside this module (compileOnly)
    compileOnly(fileTree("libs/knoxsdk.jar"))
    androidTestCompileOnly(fileTree("libs/knoxsdk.jar"))
}