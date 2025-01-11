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
include(":core:knox-feature")
include(":core:knox-feature-processor")
include(":core:knox-feature-ui")
include(":core:ui")
include(":core:testing")
include(":knox-enterprise")
include(":knox-tactical")
include(":feature:ethernet")
include(":core:knox-api")
include(":core:knox-feature-compose")
include(":core:knox-feature-hilt")
include(":core:knox-android")
