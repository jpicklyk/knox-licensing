
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "net.sfelabs.knox_common"
    compileSdk = 33

    defaultConfig {
        minSdk = 29
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    //The common Knox SDK and its APIs are available to other modules that depend on knox-common
    api(fileTree("libs/knoxsdk_ver38.jar"))
    implementation(project(path =":core:common"))
    //implementation(project(path = ":knox-tactical"))
    testImplementation(libs.testing.kotlin.coroutines)
    androidTestImplementation(libs.testing.kotlin.coroutines)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}