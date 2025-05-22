plugins {
    id("sfelabs.android.application")
    id("sfelabs.android.application.compose")
    id("sfelabs.android.hilt")
    alias(libs.plugins.android.junit5)


    //id("com.android.application")
    //id("org.jetbrains.kotlin.android")
    //id("kotlin-kapt")
    //id("dagger.hilt.android.plugin")
}

android {
    namespace = "net.sfelabs.knoxmoduleshowcase"

    defaultConfig {

        testInstrumentationRunnerArguments += mapOf("runnerBuilder" to "de.mannodermaus.junit5.AndroidJUnit5Builder")
        applicationId = "net.sfelabs.knoxmoduleshowcase"

        versionCode = 1
        versionName = "0.0.1"

        //testInstrumentationRunner = "net.sfelabs.knox_tactical.TacticalJUnitRunner"
        testInstrumentationRunner = "net.sfelabs.knoxmoduleshowcase.di.HiltTestRunner"
        // Use JUnit 5 for local unit tests

        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += arrayOf(
                "/META-INF/{LICENSE.md,LICENSE-notice.md}",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "/META-INF/gradle/incremental.annotation.processors"
            )

            pickFirsts.add("com/samsung/android/knox/**/*.class")
        }
    }

    testOptions {

        unitTests {

            isIncludeAndroidResources = true
        }
    }

}
dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":knox-core:common"))
    implementation(project(":knox-core:android"))
    implementation(project(":knox-core:usecase-executor"))
    implementation(project(":knox-core:feature"))
    implementation(project(":knox-core:feature-hilt"))
    implementation(project(":knox-core:ui"))
    //implementation(project(":android-log-wrapper"))
    implementation(project(":knox-enterprise"))
    implementation(project(":knox-tactical"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.bundles.androidx.compose.materialIcons)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigationSuite)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.kotlin.reflect)
    implementation(libs.composeNumberPicker)
    implementation(libs.google.ar)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.jackson.module)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    androidTestImplementation(project(":knox-core:testing"))
    androidTestImplementation(project(":knox-core:usecase-executor"))
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.junit.jupiter.api)
    androidTestRuntimeOnly(libs.junit.jupiter.engine)
    androidTestRuntimeOnly(libs.junit.extensions)
    androidTestImplementation(libs.junit.platform.suite)
    androidTestImplementation(libs.junit.platform.runner)
}
