import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class KnoxLicenseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                plugins.hasPlugin("com.android.application") -> {
                    extensions.configure<ApplicationExtension> {
                        configureApplicationBuildConfig(this)
                    }
                }
                plugins.hasPlugin("com.android.library") -> {
                    extensions.configure<LibraryExtension> {
                        configureLibraryBuildConfig(this)
                    }
                }
                else -> {
                    throw IllegalStateException("The 'convention.android.knox.license' plugin can only be applied to Android application or library projects.")
                }
            }
        }
    }

    private fun Project.configureApplicationBuildConfig(extension: ApplicationExtension) {
        extension.apply {
            buildFeatures {
                buildConfig = true
            }
            defaultConfig {
                val defaultKey = getKnoxLicenseKey()
                val namedKeys = getNamedLicenseKeys()

                // Default/commercial license key
                buildConfigField(
                    type = "String",
                    name = "KNOX_LICENSE_KEY",
                    value = "\"${defaultKey}\""
                )

                // Array of named keys in "name:key" format for LicenseKeyProvider
                buildConfigField(
                    type = "String[]",
                    name = "KNOX_LICENSE_KEYS",
                    value = namedKeys.toArrayLiteral()
                )
            }
        }
    }

    private fun Project.configureLibraryBuildConfig(extension: LibraryExtension) {
        extension.apply {
            buildFeatures {
                buildConfig = true
            }
            defaultConfig {
                val defaultKey = getKnoxLicenseKey()
                val namedKeys = getNamedLicenseKeys()

                // Default/commercial license key
                buildConfigField(
                    type = "String",
                    name = "KNOX_LICENSE_KEY",
                    value = "\"${defaultKey}\""
                )

                // Array of named keys in "name:key" format for LicenseKeyProvider
                buildConfigField(
                    type = "String[]",
                    name = "KNOX_LICENSE_KEYS",
                    value = namedKeys.toArrayLiteral()
                )
            }
        }
    }

    private fun Project.getKnoxLicenseKey(): String {
        return getPropertyFromLocalProperties(
            key = "knox.license",
            defaultValue = "KNOX_LICENSE_KEY_NOT_FOUND"
        )
    }

    /**
     * Reads all knox.license.* properties and returns them as name:key pairs.
     * Example: knox.license.tactical=KEY123 -> "tactical:KEY123"
     */
    private fun Project.getNamedLicenseKeys(): List<String> {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                localProperties.load(stream)
            }
        }

        return localProperties.entries
            .filter { (key, _) ->
                key.toString().startsWith("knox.license.") &&
                key.toString() != "knox.license"
            }
            .map { (key, value) ->
                val name = key.toString().removePrefix("knox.license.")
                "$name:$value"
            }
    }

    private fun List<String>.toArrayLiteral(): String {
        return if (isEmpty()) {
            "{}"
        } else {
            joinToString(prefix = "{", postfix = "}") { "\"$it\"" }
        }
    }

    private fun Project.getPropertyFromLocalProperties(key: String, defaultValue: String = ""): String {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                localProperties.load(stream)
            }
        }
        return localProperties.getProperty(key, defaultValue)
    }
}