plugins {
    id("sfelabs.android.feature")

}

android {
    namespace = "net.sfelabs.knox_ngd2"
}

dependencies {
    implementation(libs.kotlin.reflect)

    /*implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)*/
    implementation(project(mapOf("path" to ":android-log-wrapper")))
    //The Tactical Knox SDK shall not be available outside this module (compileOnly)
    compileOnly(fileTree("libs/knoxsdk.jar"))
    androidTestCompileOnly(fileTree("libs/knoxsdk.jar"))
}