plugins {
    //alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)
    id("sfelabs.android.library")
    id("sfelabs.android.library.compose")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "net.sfelabs.core.ui"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    api(libs.androidx.compose.material3)
    /*
    api(libs.androidx.compose.foundation)

    api(libs.androidx.compose.runtime)
    */
    api(libs.androidx.lifecycle.vm.comp)

    //debugImplementation(libs.androidx.compose.ui.tooling.preview)
    //implementation(libs.androidx.compose.ui.tooling.preview)


    androidTestImplementation(project(":core:testing"))
}