plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.library.compose)
    alias(libs.plugins.convention.android.hilt)
}

android {
    namespace = "com.samsung.sea.jpicklyk.tacticalqa.core.testing"
}

dependencies {

    implementation(kotlin("test"))
    implementation(libs.kotlinx.coroutines.test)
    implementation(project(":core:common"))
    implementation(libs.androidx.compose.ui.test)

    debugApi(libs.androidx.compose.ui.testManifest)

    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.datetime)
    implementation(project(":core:common"))
}
