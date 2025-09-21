
plugins {
    id("sfelabs.android.feature")
}

android {
    namespace = "net.sfelabs.knox_enterprise"
}

dependencies {
    implementation(project(":knox-core:usecase-executor"))
    implementation(project(":knox-core:android"))
    implementation(libs.spongycastle.prov)
    implementation(libs.commons.lang)
    implementation(files("libs/v3blob-validator-library-1.2.jar"))
    //The common Knox SDK and its APIs are available to other modules that depend on knox-enterprise
    api(files("libs/knoxsdk_ver38.jar"))
}