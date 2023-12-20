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

include( ":app")
include(":android-log-wrapper")
include(":core:common")
include(":core:ui")
include(":core:testing")
include(":knox-common")
include(":knox-tactical")

include(":feature:ethernet")
