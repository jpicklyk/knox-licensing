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

    api(libs.androidx.compose.foundation)
    //api(libs.androidx.compose.foundation.layout)
    //api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.vm.comp)
    //api(libs.androidx.compose.runtime.livedata)
    //api(libs.androidx.compose.ui.tooling.preview)
    //api(libs.androidx.compose.ui.util)

    androidTestImplementation(project(":core:testing"))
}