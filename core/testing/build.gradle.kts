@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    //alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)
    id("sfelabs.android.library")
    id("sfelabs.android.library.compose")
    id("sfelabs.android.hilt")
}

android {
    namespace = "net.sfelabs.core.testing"
}

dependencies {
    api(libs.androidx.compose.ui.test)
    api(libs.androidx.test.core)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.rules)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.uiautomator)
    api(libs.hilt.android.testing)
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)

    implementation(project(":core:common"))
}