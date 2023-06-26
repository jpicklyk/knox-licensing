pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

}
rootProject.name = "KnoxModuleShowcase"

include(
    ":app",
    ":android-log-wrapper",
    ":core:common",
    ":core:ui",
    ":core:testing",
    ":knox-common",
    ":knox-tactical",
    ":knox-ngd2"
)

include(":core:testing")
