plugins {
    id("sfelabs.android.library")
    id("sfelabs.android.hilt")
    id("sfelabs.android.library.compose")
    //id("dagger.hilt.android.plugin")
    //id("kotlin-kapt")
}

android {
    namespace = "net.sfelabs.core.common"
}

dependencies {
    implementation(libs.androidx.collections)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.jackson.module)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.lifecycle.vm.comp)
    testImplementation(libs.androidx.ui.android)
    //implementation(libs.hilt.android)
    //kapt("com.google.dagger:hilt-compiler")
    androidTestImplementation(project(":core:testing"))
    testImplementation(libs.kotlin.stdlib)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.mockk.android)

}

