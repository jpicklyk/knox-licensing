plugins {
    id("sfelabs.android.application")
    id("sfelabs.android.application.compose")
    id("sfelabs.android.hilt")


    //id("com.android.application")
    //id("org.jetbrains.kotlin.android")
    //id("kotlin-kapt")
    //id("dagger.hilt.android.plugin")
}

android {
    namespace = "net.sfelabs.knoxmoduleshowcase"

    sourceSets {
        getByName("main") {
            aidl.srcDirs("src/main/aidl")
        }
    }

    buildFeatures {
        aidl = true
    }

    defaultConfig {

        applicationId = "net.sfelabs.knoxmoduleshowcase"

        versionCode = 1
        versionName = "0.0.1"

        //testInstrumentationRunner = "net.sfelabs.knox_tactical.TacticalJUnitRunner"
        testInstrumentationRunner = "net.sfelabs.knoxmoduleshowcase.di.HiltTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    // Enable baseline profile generation
    experimentalProperties["android.experimental.enableBaselineProfileGeneration"] = true

    signingConfigs {
        create("release") {
            // Use debug signing for baseline profiles
            storeFile = signingConfigs.getByName("debug").storeFile
            storePassword = signingConfigs.getByName("debug").storePassword
            keyAlias = signingConfigs.getByName("debug").keyAlias
            keyPassword = signingConfigs.getByName("debug").keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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
    implementation(project(":knox-licensing"))

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
    implementation(libs.androidx.profileinstaller)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)

    androidTestImplementation(project(":knox-core:testing"))
    androidTestImplementation(project(":knox-core:usecase-executor"))
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
