plugins {
    `kotlin-dsl`
}


//group = "net.sfelabs.knoxmoduleshowcase.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sfelabs.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }

        register("androidApplicationCompose") {
            id = "sfelabs.android.application.compose"
            implementationClass = "AndroidApplicationComposePlugin"
        }

        register("androidHilt") {
            id = "sfelabs.android.hilt"
            implementationClass = "AndroidHiltPlugin"
        }
    }
}