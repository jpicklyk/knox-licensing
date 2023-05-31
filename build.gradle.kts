// Top-level build file where you can add configuration options common to all sub-projects/modules.
/* "libs" Incorrectly marked as an error in the ide.  Tracking via:
 * https://youtrack.jetbrains.com/issue/KTIJ-19369
 *
 * For now use @Suppress("DSL_SCOPE_VIOLATION) to work around the issue
 */
//@file:Suppress("DSL_SCOPE_VIOLATION")
// All project dependencies tracked in /gradle/libs.versions.toml
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) version "8.2.0-alpha06" apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
}
