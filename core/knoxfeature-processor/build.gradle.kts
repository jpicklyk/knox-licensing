plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
    //implementation(project(":core:common"))
    implementation(project(":core:knoxfeature"))  // For shared models/interfaces
}
