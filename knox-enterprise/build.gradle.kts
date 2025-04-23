
plugins {
    id("sfelabs.android.feature")
    alias(libs.plugins.convention.android.knox.license)
}

android {
    namespace = "net.sfelabs.knox_enterprise"
}

dependencies {
    implementation(project(":knox-core:usecase-executor"))
    implementation(project(":knox-core:android"))
    api(libs.spongycastle.prov)
    api(libs.commons.lang)
    //The common Knox SDK and its APIs are available to other modules that depend on knox-enterprise
    api(files("libs/knoxsdk_ver38.jar"))
    api(files("libs/v3blob-validator-library-1.2.jar"))
}