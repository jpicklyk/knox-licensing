plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:usecase-executor"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
    implementation(libs.ksp.gradlePlugin)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    //TODO: Remove once all features are migrated to new format
    implementation(libs.jackson.module)
}