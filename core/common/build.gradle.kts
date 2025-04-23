plugins {
    alias(libs.plugins.convention.android.library)
    //alias(libs.plugins.convention.android.library.jacoco)
    alias(libs.plugins.convention.android.hilt)
}

android {
    namespace = "com.samsung.sea.jpicklyk.tacticalqa.core.common"
}


dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
}
