//@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    //alias(libs.plugins.android.library)
    //alias(libs.plugins.kotlin.android)
    id("sfelabs.android.library.compose")
    id("sfelabs.android.feature")
}

android {
    namespace = "net.sfelabs.knoxmoduleshowcase.feature.ethernet"

}
