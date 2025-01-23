plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.convention.android.library.compose)
}

android {
    namespace = "com.example.knox.feature.compose"
}

dependencies {

    implementation(libs.androidx.compose.material.iconsExt)
    implementation(project(":core:knox-feature"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}