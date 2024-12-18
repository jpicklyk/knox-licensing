plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.convention.hilt)
}

android {
    namespace = "net.sfelabs.core.knox.android"

}

dependencies {
    implementation(project(":core:knox-api"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}