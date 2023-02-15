//Not required to enable in Gradle 7.4+ as it is enabled by default
//enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "KnoxModuleShowcase"


include(
    ":app",
    ":core:common",
    ":android-log-wrapper",
    ":knox-common",
    ":knox-tactical",
    ":android-log-wrapper"
)
pluginManagement {
    //includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

}

include(":lib")
include(":knox-ngd2")
