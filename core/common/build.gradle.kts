plugins {
    id("sfelabs.android.library")
    id("sfelabs.android.hilt")
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
    //implementation(libs.hilt.android)
    //kapt("com.google.dagger:hilt-compiler")
    androidTestImplementation(project(":core:testing"))
}

