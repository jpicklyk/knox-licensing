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
include(":core:common")
include(":core:designsystem")
include(":core:testing")
include(":knox-core:android")
include(":knox-core:common")
include(":knox-core:feature")
include(":knox-core:feature-hilt")
include(":knox-core:feature-processor")
include(":knox-core:testing")
include(":knox-core:ui")
include(":knox-core:usecase-executor")
include(":knox-enterprise")
include(":knox-tactical")
include(":feature:ethernet")
include(":benchmark")



