import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.convention.android.library)
}

extensions.configure<LibraryExtension> {
    namespace = "com.github.jpicklyk.knox.licensing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "KNOX_LICENSE_KEY", "\"KNOX_LICENSE_KEY_NOT_FOUND\"")
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", "{}")
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Knox SDK - compileOnly so consumers provide their own SDK JAR
    // This avoids conflicts when knox-tactical provides its Tactical SDK
    compileOnly(files("libs/knoxsdk_ver38.jar"))

    // Android Core
    implementation(libs.androidx.core.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.core)

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}