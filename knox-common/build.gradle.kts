
plugins {
    id("sfelabs.android.feature")
}

android {
    namespace = "net.sfelabs.knox_common"
}

dependencies {
    api(libs.spongycastle.prov)
    api(libs.commons.lang)
    //The common Knox SDK and its APIs are available to other modules that depend on knox-common
    api(fileTree("libs/knoxsdk_ver38.jar"))
    api(fileTree("libs/v3blob-validator-library-1.2.jar"))
}