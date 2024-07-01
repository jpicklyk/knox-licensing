plugins {
    id("sfelabs.android.library")
    id("sfelabs.android.hilt")
}

android {
    namespace = "net.sfelabs.core.common"
}

dependencies {
    implementation(project(":core:common"))
    testImplementation(libs.konsist)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}