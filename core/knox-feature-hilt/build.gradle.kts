plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.convention.hilt)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "net.sfelabs.core.knox.feature.hilt"

    packaging {
        resources {
            excludes += arrayOf(
                "/META-INF/{LICENSE.md,LICENSE-notice.md}"
            )
        }
    }
}

dependencies {
    implementation(project(":core:knox-api"))
    implementation(project(":core:knox-feature"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}