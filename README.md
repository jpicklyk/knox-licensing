# Knox Licensing for Android

A reusable Android library for Samsung Knox Enterprise License Management that provides a clean, coroutine-based API for license activation, deactivation, and monitoring.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Knox License Setup](#knox-license-setup)
- [Usage](#usage)
  - [Basic Usage](#basic-usage)
  - [License Selection Strategy](#license-selection-strategy)
  - [License Operations](#license-operations)
  - [License State Monitoring](#license-state-monitoring)
  - [Available License Management](#available-license-management)
  - [Startup Manager](#startup-manager)
- [Configuration](#configuration)
  - [Using BuildConfig (Recommended)](#using-buildconfig-recommended)
  - [Multi-Module Projects](#multi-module-projects)
  - [Custom License Selection](#custom-license-selection)
  - [Manual Configuration](#manual-configuration)
- [Dependency Injection Integration](#dependency-injection-integration)
  - [Hilt Integration](#hilt-integration)
  - [Koin Integration](#koin-integration)
- [API Reference](#api-reference)
- [Requirements](#requirements)
  - [Dependencies](#dependencies)
  - [Device Administrator Requirements](#device-administrator-requirements)
- [Error Handling](#error-handling)
- [Security Considerations](#security-considerations)
- [Performance Considerations](#performance-considerations)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

- **Clean Architecture**: Separation of domain and data layers
- **Coroutines Support**: Async/await license operations with Flow-based state monitoring
- **Named License Keys**: Support for multiple named license configurations
- **Configurable License Selection**: Pluggable strategy pattern for custom license selection logic
- **Device-Agnostic**: No hardcoded device detection dependencies
- **Comprehensive Error Handling**: Detailed Knox SDK error code mapping
- **Framework Agnostic**: No dependency injection framework required
- **Easy Integration**: Simple factory pattern for instantiation

## Installation

Add this module as a git submodule to your Android project:

```bash
git submodule add https://github.com/jpicklyk/knox-licensing.git knox-licensing
```

Include the module in your `settings.gradle.kts`:

```kotlin
include(":knox-licensing")
```

Add the dependency in your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":knox-licensing"))
}
```

## Knox License Setup

Before using Knox policies on a device, you need a Knox Platform for Enterprise (KPE) license:

1. **Create a Samsung Knox Developer account** at [developer.samsungknox.com](https://developer.samsungknox.com/)

2. **Obtain a KPE license key** — follow the [Knox Platform for Enterprise license guide](https://docs.samsungknox.com/admin/knox-platform-for-enterprise/before-you-begin/knox-platform-for-enterprise-licenses/) to generate your license key

3. **Add the license to `local.properties`** in the project root:

   ```properties
   knox.license=KLM09-XXXX...XXX
   ```

4. **Set a unique package name** — your application's package name must be unique and match what is registered with your license. Use the rename scripts to update the package name across the codebase:

   - **Windows**: `.\UpdatePackageName.ps1`
   - **Mac/Linux**: `./update_package_name.sh`

5. **Bind the application to your license** — the compiled application must be bound to your license key in the Knox console for the activation process to succeed. Register your app's package name and signing certificate in your [Knox developer portal](https://developer.samsungknox.com/).

## Usage

### Basic Usage

```kotlin
import com.github.jpicklyk.knox.licensing.KnoxLicenseFactory
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseState
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy

class MainActivity : AppCompatActivity() {
    private lateinit var knoxLicenseHandler: KnoxLicenseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create handler from BuildConfig (uses default license key)
        knoxLicenseHandler = KnoxLicenseFactory.createFromBuildConfig(this)

        // Create with custom license selection strategy
        val customStrategy = MyLicenseSelectionStrategy()
        knoxLicenseHandler = KnoxLicenseFactory.create(this, customStrategy)

        // Create with app's BuildConfig (when used in multi-module projects)
        knoxLicenseHandler = KnoxLicenseFactory.create(
            context = this,
            licenseSelectionStrategy = customStrategy,
            defaultKey = BuildConfig.KNOX_LICENSE_KEY,
            namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS
        )

        // Or create with explicit keys
        knoxLicenseHandler = KnoxLicenseFactory.createWithKeys(
            context = this,
            defaultKey = "KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX",
            namedKeys = mapOf(
                "production" to "KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY",
                "enterprise" to "KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ"
            )
        )
    }
}
```

### License Selection Strategy

The library supports custom license selection logic through the `LicenseSelectionStrategy` interface. This allows applications to implement device-specific or context-specific license selection without creating dependencies.

```kotlin
import android.os.Build
import android.content.pm.PackageManager
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy

class MyDeviceBasedStrategy : LicenseSelectionStrategy {
    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        return when {
            isProductionDevice() -> availableKeys["production"] ?: defaultKey
            isEnterpriseDevice() -> availableKeys["enterprise"] ?: defaultKey
            else -> defaultKey
        }
    }

    private fun isProductionDevice(): Boolean {
        // Example production device detection logic
        return Build.MODEL.contains("PROD") ||
               Build.DISPLAY.contains("RELEASE") ||
               !BuildConfig.DEBUG
    }

    private fun isEnterpriseDevice(): Boolean {
        // Example enterprise device detection logic
        return try {
            // Check for enterprise features or specific device configurations
            Build.MODEL.contains("ENTERPRISE") ||
            Build.BRAND.equals("SAMSUNG", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }
}

// Use the strategy
val strategy = MyDeviceBasedStrategy()
val handler = KnoxLicenseFactory.create(context, strategy)
```

#### Strategy Benefits

- **Flexibility**: Implement any device detection or selection logic
- **Testability**: Easy to unit test different selection scenarios
- **Independence**: No dependencies on specific device detection libraries
- **Reusability**: Same strategy can be used across different apps

#### Example: Custom SDK Module License Selection

When your project uses a custom Knox SDK module (e.g., `knox-custom-sdk-module`) that requires a different license than the standard commercial license, implement a device-aware strategy:

```kotlin
/**
 * License selection strategy that chooses between commercial and custom SDK licenses
 * based on device characteristics.
 */
class CustomSdkLicenseSelectionStrategy : LicenseSelectionStrategy {

    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        return if (isCustomSdkDevice()) {
            // Use custom SDK license for specialized devices
            availableKeys["custom-sdk"] ?: defaultKey
        } else {
            // Use standard commercial license for regular devices
            defaultKey
        }
    }

    /**
     * Detect if device requires custom SDK license.
     * Implement your device-specific detection logic here.
     */
    private fun isCustomSdkDevice(): Boolean {
        // Example: Check for specific device models or configurations
        val model = Build.MODEL.uppercase()
        return model.contains("CUSTOM") ||
               model.contains("SPECIALIZED") ||
               hasCustomSdkFeature()
    }

    private fun hasCustomSdkFeature(): Boolean {
        // Check for device-specific features that indicate custom SDK requirement
        return try {
            // Example: Check system property or feature flag
            val prop = System.getProperty("ro.custom.sdk.enabled", "false")
            prop.equals("true", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }
}

// Provide via Hilt DI
@Module
@InstallIn(SingletonComponent::class)
object AppLicensingModule {
    @Provides
    @Singleton
    fun provideLicenseSelectionStrategy(): LicenseSelectionStrategy {
        return CustomSdkLicenseSelectionStrategy()
    }
}
```

With the corresponding `local.properties` configuration:

```properties
# Standard commercial Knox license
knox.license=KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX

# Custom SDK license for specialized devices
knox.license.custom-sdk=KLM09-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
```

### License Operations

```kotlin
// Activate default license
lifecycleScope.launch {
    when (val result = knoxLicenseHandler.activate()) {
        is LicenseResult.Success -> {
            Log.d("Knox", "License activated: ${result.message}")
        }
        is LicenseResult.Error -> {
            Log.e("Knox", "Activation failed: ${result.message}")
        }
    }
}

// Activate named license
lifecycleScope.launch {
    val result = knoxLicenseHandler.activate("production")
    // Handle result...
}

// Deactivate license
lifecycleScope.launch {
    val result = knoxLicenseHandler.deactivate()
    // Handle result...
}

// Get license information
lifecycleScope.launch {
    val licenseInfo = knoxLicenseHandler.getLicenseInfo()
    Log.d("Knox", "License active: ${licenseInfo.isActivated}")
}
```

### License State Monitoring

```kotlin
// Observe license state changes
lifecycleScope.launch {
    knoxLicenseHandler.observeLicenseState().collect { state ->
        when (state) {
            is LicenseState.Loading -> {
                // Show loading indicator
            }
            is LicenseState.Activated -> {
                Log.d("Knox", "License activated: ${state.message}")
            }
            is LicenseState.Deactivated -> {
                Log.d("Knox", "License deactivated: ${state.message}")
            }
            is LicenseState.Error -> {
                Log.e("Knox", "License error: ${state.message}")
            }
        }
    }
}
```

### Available License Management

```kotlin
// Check available licenses
val availableLicenses = knoxLicenseHandler.getAvailableLicenses()
availableLicenses.forEach { (name, key) ->
    Log.d("Knox", "License '$name': ${key.take(10)}...")
}

// Check if specific license exists
if (knoxLicenseHandler.hasLicense("production")) {
    // Production license is configured
}
```

### Startup Manager

The library provides two approaches for license initialization:

#### KnoxLicenseInitializer (Recommended for Hilt/DI)

`KnoxLicenseInitializer` is a class that can be injected via Hilt or other DI frameworks:

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var licenseInitializer: KnoxLicenseInitializer

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            val result = licenseInitializer.initialize(this@MyApplication)
            // Handle result...
        }
    }
}

// In ViewModels, observe the reactive status
@HiltViewModel
class MainViewModel @Inject constructor(
    private val licenseInitializer: KnoxLicenseInitializer
) : ViewModel() {
    val licenseStatus: StateFlow<LicenseStartupResult> = licenseInitializer.licenseStatus
}
```

> **Note:** If using knox-hilt, `KnoxLicenseInitializer` is automatically provided by `KnoxLicensingModule`.

#### KnoxStartupManager (For non-DI usage)

`KnoxStartupManager` provides static methods for convenient initialization without DI:

```kotlin
import com.github.jpicklyk.knox.licensing.domain.KnoxStartupManager
import com.github.jpicklyk.knox.licensing.domain.LicenseStartupResult
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Knox licensing at startup with default configuration
        lifecycleScope.launch {
            when (val result = KnoxStartupManager.initializeKnoxLicensing(this@MyApplication)) {
                // Handle results...
            }
        }

        // Or initialize with custom license selection strategy
        lifecycleScope.launch {
            val customStrategy = MyDeviceBasedStrategy()
            when (val result = KnoxStartupManager.initializeKnoxLicensing(this@MyApplication, customStrategy)) {
                // Handle results...
            }
        }

        // Or initialize with app's BuildConfig (for multi-module projects)
        lifecycleScope.launch {
            val customStrategy = MyDeviceBasedStrategy()
            when (val result = KnoxStartupManager.initializeKnoxLicensing(
                context = this@MyApplication,
                licenseSelectionStrategy = customStrategy,
                defaultKey = BuildConfig.KNOX_LICENSE_KEY,
                namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS
            )) {
                is LicenseStartupResult.AlreadyActivated -> {
                    Log.d("Knox", "License was already activated")
                }
                is LicenseStartupResult.ActivatedNow -> {
                    Log.d("Knox", "License activated successfully")
                }
                is LicenseStartupResult.ActivationFailed -> {
                    Log.e("Knox", "License activation failed: ${result.reason}")
                }
                is LicenseStartupResult.InitializationError -> {
                    Log.e("Knox", "Initialization error: ${result.reason}")
                }
                LicenseStartupResult.NotChecked -> {
                    Log.w("Knox", "License status not checked")
                }
            }
        }
    }
}
```

#### Startup Manager Features

```kotlin
// Check if Knox licensing is ready for use
if (KnoxStartupManager.isKnoxLicenseReady()) {
    // Proceed with Knox operations
}

// Get current license status
val status = KnoxStartupManager.getLicenseStatus()

// Reset startup manager (useful for testing)
KnoxStartupManager.reset()
```

The startup manager automatically:
- Uses custom license selection strategy if provided
- Checks if license is already activated before attempting activation
- Provides detailed status reporting
- Handles initialization errors gracefully
- Uses singleton pattern to avoid duplicate initialization

## Configuration

### Using a Gradle Convention Plugin (Recommended for Multi-Module Projects)

For multi-module Android projects, creating a Gradle convention plugin is the cleanest approach. This plugin reads license keys from `local.properties` and injects them into your app's BuildConfig.

#### Step 1: Create the Convention Plugin

Create `KnoxLicenseConventionPlugin.kt` in your `build-logic/convention/src/main/kotlin/`:

```kotlin
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
                        buildFeatures { buildConfig = true }
                        defaultConfig {
                            val defaultKey = getKnoxLicenseKey()
                            val namedKeys = getNamedLicenseKeys()

                            // Default/commercial license key
                            buildConfigField("String", "KNOX_LICENSE_KEY", "\"$defaultKey\"")

                            // Array of named keys in "name:key" format for LicenseKeyProvider
                            buildConfigField("String[]", "KNOX_LICENSE_KEYS", namedKeys.toArrayLiteral())
                        }
                    }
                }
                plugins.hasPlugin("com.android.library") -> {
                    // Similar configuration for library modules
                }
            }
        }
    }

    private fun Project.getKnoxLicenseKey(): String {
        return getPropertyFromLocalProperties("knox.license", "KNOX_LICENSE_KEY_NOT_FOUND")
    }

    /**
     * Reads all knox.license.* properties and returns them as name:key pairs.
     * Example: knox.license.custom-sdk=KEY123 -> "custom-sdk:KEY123"
     */
    private fun Project.getNamedLicenseKeys(): List<String> {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
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
        return if (isEmpty()) "{}" else joinToString(prefix = "{", postfix = "}") { "\"$it\"" }
    }

    private fun Project.getPropertyFromLocalProperties(key: String, defaultValue: String): String {
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        return localProperties.getProperty(key, defaultValue)
    }
}
```

#### Step 2: Register the Plugin

In your `build-logic/convention/build.gradle.kts`:

```kotlin
gradlePlugin {
    plugins {
        register("knoxLicense") {
            id = "convention.android.knox.license"
            implementationClass = "KnoxLicenseConventionPlugin"
        }
    }
}
```

#### Step 3: Apply the Plugin

In your app module's `build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application")
    id("convention.android.knox.license")  // Apply the Knox license plugin
}
```

#### Step 4: Configure License Keys

Add to your `local.properties` (never commit to version control):

```properties
# Default/commercial license key
knox.license=KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX

# Named license keys (any knox.license.* property is automatically picked up)
knox.license.custom-sdk=KLM09-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
knox.license.enterprise=KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY
knox.license.development=KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ
```

The plugin automatically discovers all `knox.license.*` properties and generates the `KNOX_LICENSE_KEYS` array in the format expected by `LicenseKeyProvider`:
- `knox.license.custom-sdk` → `"custom-sdk:KLM09-..."`
- `knox.license.enterprise` → `"enterprise:KLM06-..."`

This approach ensures license keys are:
- Centrally managed in `local.properties`
- Automatically injected into BuildConfig
- Available for license selection strategies
- Never committed to version control

### Product Flavors for Customer-Specific Licensing

When deploying to multiple customers who each require their own Knox license, use Android product flavors to manage customer-specific configurations.

#### Step 1: Define Product Flavors

In your app's `build.gradle.kts`:

```kotlin
android {
    flavorDimensions += "customer"

    productFlavors {
        create("customerA") {
            dimension = "customer"
            applicationIdSuffix = ".customera"
        }
        create("customerB") {
            dimension = "customer"
            applicationIdSuffix = ".customerb"
        }
        create("internal") {
            dimension = "customer"
            // Internal/development builds
        }
    }
}
```

#### Step 2: Configure Customer-Specific License Keys

In your `local.properties`:

```properties
# Default/fallback license (used if flavor-specific key not found)
knox.license=KLM06-DEFAULT-XXXXX-XXXXX-XXXXX-XXXXX

# Customer-specific license keys
knox.license.customerA=KLM06-CUSTA-XXXXX-XXXXX-XXXXX-XXXXX
knox.license.customerB=KLM06-CUSTB-XXXXX-XXXXX-XXXXX-XXXXX
knox.license.internal=KLM06-INTERNAL-XXXXX-XXXXX-XXXXX

# Customer + device-specific keys (for custom SDK devices per customer)
knox.license.customerA.custom-sdk=KLM09-CUSTA-CUSTOM-XXXXX-XXXXX
knox.license.customerB.custom-sdk=KLM09-CUSTB-CUSTOM-XXXXX-XXXXX
```

#### Step 3: Enhance Convention Plugin for Flavors

Extend your convention plugin to configure flavor-specific BuildConfig fields:

```kotlin
private fun Project.configureApplicationBuildConfig(extension: ApplicationExtension) {
    extension.apply {
        buildFeatures { buildConfig = true }

        // Default configuration (fallback)
        defaultConfig {
            val defaultKey = getKnoxLicenseKey("knox.license")
            buildConfigField("String", "KNOX_LICENSE_KEY", "\"$defaultKey\"")
            buildConfigField("String[]", "KNOX_LICENSE_KEYS", getNamedLicenseKeys().toArrayLiteral())
        }

        // Configure each product flavor with its specific license
        productFlavors.configureEach {
            val flavorName = this.name
            val flavorKey = getKnoxLicenseKey("knox.license.$flavorName")
            val flavorNamedKeys = getNamedLicenseKeysForFlavor(flavorName)

            if (flavorKey != "KNOX_LICENSE_KEY_NOT_FOUND") {
                buildConfigField("String", "KNOX_LICENSE_KEY", "\"$flavorKey\"")
            }
            if (flavorNamedKeys.isNotEmpty()) {
                buildConfigField("String[]", "KNOX_LICENSE_KEYS", flavorNamedKeys.toArrayLiteral())
            }
        }
    }
}

/**
 * Gets named keys for a specific flavor.
 * Looks for: knox.license.<flavorName>.<keyName>=VALUE
 */
private fun Project.getNamedLicenseKeysForFlavor(flavorName: String): List<String> {
    val prefix = "knox.license.$flavorName."
    return loadLocalProperties().entries
        .filter { (key, _) -> key.toString().startsWith(prefix) }
        .map { (key, value) ->
            val keyName = key.toString().removePrefix(prefix)
            "$keyName:$value"
        }
}
```

#### Step 4: Implement Customer-Aware License Selection

Create a strategy that considers both customer (flavor) and device type:

```kotlin
class CustomerAwareLicenseStrategy : LicenseSelectionStrategy {

    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        // The defaultKey is already customer-specific (set by flavor's BuildConfig)
        // availableKeys contains customer-specific named keys (e.g., "custom-sdk")

        return when {
            isCustomSdkDevice() -> availableKeys["custom-sdk"] ?: defaultKey
            else -> defaultKey
        }
    }

    private fun isCustomSdkDevice(): Boolean {
        // Device-specific detection logic
        return Build.MODEL.contains("CUSTOM")
    }
}
```

#### Build Variant Matrix

With this setup, your build variants would be:

| Variant | License Source | Use Case |
|---------|---------------|----------|
| `customerADebug` | `knox.license.customerA` | Customer A development |
| `customerARelease` | `knox.license.customerA` | Customer A production |
| `customerBDebug` | `knox.license.customerB` | Customer B development |
| `customerBRelease` | `knox.license.customerB` | Customer B production |
| `internalDebug` | `knox.license.internal` | Internal testing |

#### CI/CD Integration

For CI/CD pipelines, inject customer-specific keys via environment variables:

```bash
# In CI pipeline
echo "knox.license.customerA=$CUSTOMER_A_LICENSE" >> local.properties
echo "knox.license.customerB=$CUSTOMER_B_LICENSE" >> local.properties

# Build specific customer variant
./gradlew assembleCustomerARelease
```

#### Security Considerations for Multi-Customer Deployments

1. **Isolate customer keys**: Each customer's license should only be in their specific build variant
2. **CI/CD secrets**: Store customer keys as separate CI/CD secrets, injected only for their builds
3. **Code signing**: Use different signing keys per customer to prevent APK tampering
4. **License auditing**: Log license activation (without the key) for compliance tracking

### Using BuildConfig Directly

Alternatively, add BuildConfig fields directly to your `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "KNOX_LICENSE_KEY", getDefaultLicenseKey())
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", getNamedLicenseKeys())
    }

    buildFeatures {
        buildConfig = true
    }
}

fun getDefaultLicenseKey(): String {
    // Manually load local.properties since project.findProperty() doesn't auto-load it
    val localPropsFile = File(project.rootDir, "local.properties")
    val localProps = Properties()
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { localProps.load(it) }
    }
    val defaultKey = localProps.getProperty("knox.license") ?: ""
    return "\"$defaultKey\""
}

fun getNamedLicenseKeys(): String {
    // Manually load local.properties
    val localPropsFile = File(project.rootDir, "local.properties")
    val localProps = Properties()
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { localProps.load(it) }
    }

    val keys = mutableListOf<String>()

    // Add named keys from properties
    localProps.getProperty("knox.license.production")?.let { key ->
        keys.add("\"production:$key\"")
    }
    localProps.getProperty("knox.license.enterprise")?.let { key ->
        keys.add("\"enterprise:$key\"")
    }

    return if (keys.isEmpty()) {
        "new String[]{}"
    } else {
        "new String[]{${keys.joinToString(", ")}}"
    }
}
```

Add to your `local.properties`:

```properties
knox.license=KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
knox.license.production=KLM09-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY
knox.license.enterprise=KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ
```

**Important Note**: Due to how Gradle handles `project.findProperty()`, it doesn't automatically load `local.properties`. The functions above manually load the properties file to ensure BuildConfig generation works correctly.

## Multi-Module Projects

When using this library in multi-module Android projects, there are two configuration approaches:

### Option 1: Knox-Licensing Module BuildConfig (Simple)

The knox-licensing module reads from its own BuildConfig. Configure license keys in the knox-licensing module's build.gradle.kts:

```kotlin
// In knox-licensing/build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "KNOX_LICENSE_KEY", getDefaultLicenseKey())
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", getNamedLicenseKeys())
    }
}
```

Then use the standard factory methods:

```kotlin
val handler = KnoxLicenseFactory.create(context, customStrategy)
```

### Option 2: App Module BuildConfig (Recommended)

The app module configures license keys and passes them to knox-licensing. This is the recommended approach for better separation of concerns:

```kotlin
// In app/build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "KNOX_LICENSE_KEY", getDefaultLicenseKey())
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", getNamedLicenseKeys())
    }
}

// In your app code
val handler = KnoxLicenseFactory.create(
    context = context,
    licenseSelectionStrategy = customStrategy,
    defaultKey = BuildConfig.KNOX_LICENSE_KEY,
    namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS
)

// Or with KnoxStartupManager
KnoxStartupManager.initializeKnoxLicensing(
    context = context,
    licenseSelectionStrategy = customStrategy,
    defaultKey = BuildConfig.KNOX_LICENSE_KEY,
    namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS
)
```

This approach ensures that license keys are managed centrally in the app module while the knox-licensing library remains configuration-agnostic.

## Custom License Selection

The library supports custom license selection through the strategy pattern:

- **Default Behavior**: Uses `knox.license` from BuildConfig
- **Custom Strategy**: Implement `LicenseSelectionStrategy` for device-specific logic
- **Named Keys**: Access configured named keys through the strategy interface

This allows applications to implement their own device detection and license selection logic without creating dependencies on specific device libraries.

### Manual Configuration

```kotlin
val licenseConfiguration = LicenseConfiguration(
    defaultKey = "KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX",
    namedKeys = mapOf(
        "production" to "KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY",
        "enterprise" to "KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ"
    )
)

val knoxLicenseHandler = KnoxLicenseFactory.create(context, licenseConfiguration)
```

## Dependency Injection Integration

The library is framework-agnostic and doesn't include any DI dependencies. You can easily integrate it with your preferred DI framework:

### Hilt Integration

If using **knox-hilt**, `KnoxLicenseInitializer` is automatically provided. You only need to provide your `LicenseSelectionStrategy`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppLicensingModule {

    @Provides
    @Singleton
    fun provideLicenseSelectionStrategy(): LicenseSelectionStrategy {
        return MyDeviceBasedStrategy()
    }
}

// Usage in your components
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var licenseInitializer: KnoxLicenseInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            licenseInitializer.initialize(this@MainActivity)
        }
    }
}
```

If **not** using knox-hilt, provide the full module yourself:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object KnoxLicenseModule {

    @Provides
    @Singleton
    fun provideLicenseSelectionStrategy(): LicenseSelectionStrategy {
        return MyDeviceBasedStrategy()
    }

    @Provides
    @Singleton
    fun provideKnoxLicenseInitializer(): KnoxLicenseInitializer {
        return KnoxLicenseInitializer().also {
            // Register for backward compatibility with KnoxStartupManager
            KnoxStartupManager.setInstance(it)
        }
    }

    @Provides
    @Singleton
    fun provideKnoxLicenseHandler(
        @ApplicationContext context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy
    ): KnoxLicenseHandler {
        return KnoxLicenseFactory.create(context, licenseSelectionStrategy)
    }
}
```

### Koin Integration

```kotlin
val knoxLicenseModule = module {
    single<LicenseSelectionStrategy> { MyDeviceBasedStrategy() }

    single<KnoxLicenseHandler> {
        KnoxLicenseFactory.create(androidContext(), get())
    }
}

// In your Application class
startKoin {
    androidContext(this@MyApplication)
    modules(knoxLicenseModule)
}

// Usage in your components
class MainActivity : AppCompatActivity() {
    private val knoxLicenseHandler: KnoxLicenseHandler by inject()

    // Use knoxLicenseHandler...
}
```

## API Reference

### Core Interfaces

#### KnoxLicenseHandler

The main interface for Knox license management operations.

```kotlin
interface KnoxLicenseHandler {
    // License activation operations
    suspend fun activate(licenseName: String = "default"): LicenseResult
    suspend fun deactivate(licenseName: String = "default"): LicenseResult

    // License information
    suspend fun getLicenseInfo(): LicenseInfo
    fun getAvailableLicenses(): Map<String, String>
    fun hasLicense(licenseName: String): Boolean

    // State monitoring
    fun observeLicenseState(): Flow<LicenseState>
}
```

#### LicenseSelectionStrategy

Interface for implementing custom license selection logic.

```kotlin
interface LicenseSelectionStrategy {
    /**
     * Selects appropriate license key based on available keys and device context
     * @param availableKeys Map of named license keys (name -> key)
     * @param defaultKey The default license key to fall back to
     * @return Selected license key string
     */
    fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String
}
```

### Data Classes

#### LicenseResult

Sealed class representing the result of license operations.

```kotlin
sealed class LicenseResult {
    data class Success(val message: String) : LicenseResult()
    data class Error(val message: String, val errorCode: Int = -1) : LicenseResult()
}
```

#### LicenseState

Sealed class representing the current state of license.

```kotlin
sealed class LicenseState {
    object Loading : LicenseState()
    data class Activated(val message: String) : LicenseState()
    data class Deactivated(val message: String) : LicenseState()
    data class Error(val message: String) : LicenseState()
}
```

#### LicenseInfo

Data class containing license information.

```kotlin
data class LicenseInfo(
    val isActivated: Boolean,
    val licenseKey: String? = null,
    val activationDate: String? = null,
    val expirationDate: String? = null,
    val errorCode: Int? = null,
    val errorMessage: String? = null
)
```

#### LicenseConfiguration

Data class for license configuration.

```kotlin
data class LicenseConfiguration(
    val defaultKey: String,
    val namedKeys: Map<String, String> = emptyMap()
) {
    fun getKey(licenseName: String): String
    fun getAllKeyNames(): Set<String>
}
```

### Factory Methods

#### KnoxLicenseFactory

Factory class for creating KnoxLicenseHandler instances.

```kotlin
object KnoxLicenseFactory {
    // Create from BuildConfig
    fun createFromBuildConfig(context: Context): KnoxLicenseHandler

    // Create with license selection strategy
    fun create(context: Context, licenseSelectionStrategy: LicenseSelectionStrategy?): KnoxLicenseHandler

    // Create with app BuildConfig (multi-module)
    fun create(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy?,
        defaultKey: String,
        namedKeysArray: Array<String>?
    ): KnoxLicenseHandler

    // Create with explicit configuration
    fun create(context: Context, licenseConfiguration: LicenseConfiguration): KnoxLicenseHandler

    // Create with explicit keys
    fun createWithKeys(
        context: Context,
        defaultKey: String,
        namedKeys: Map<String, String> = emptyMap()
    ): KnoxLicenseHandler
}
```

### Startup Manager

#### KnoxStartupManager

Utility for managing Knox license initialization during app startup.

```kotlin
object KnoxStartupManager {
    // Initialize with default configuration
    suspend fun initializeKnoxLicensing(context: Context): LicenseStartupResult

    // Initialize with custom strategy
    suspend fun initializeKnoxLicensing(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy?
    ): LicenseStartupResult

    // Initialize with app BuildConfig
    suspend fun initializeKnoxLicensing(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy?,
        defaultKey: String,
        namedKeysArray: Array<String>?
    ): LicenseStartupResult

    // Status checking
    fun isKnoxLicenseReady(): Boolean
    fun getLicenseStatus(): LicenseStartupResult
    fun reset()
}
```

## Requirements

- Android API 21+
- Samsung Knox-enabled device
- Knox Enterprise License Manager
- **Device Administrator or Device Owner/Profile Owner privileges** (required for license operations)

### Dependencies

The module requires the following dependencies. If you're creating your own `libs.versions.toml`, use these naming conventions:

#### Version Catalog (`libs.versions.toml`)

```toml
[versions]
androidx-core = "1.15.0"           # Or latest stable
kotlinx-coroutines = "1.10.2"     # Or latest stable
junit4 = "4.13.2"
mockk-version = "1.14.7"
testing-androidx-junit = "1.2.1"
testing-androidx-espresso = "3.6.1"

[libraries]
# Required runtime dependencies
androidx-core-ktx = { module = "androidx.core:core-ktx", version.ref = "androidx-core" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinx-coroutines" }

# Testing dependencies (optional)
junit = { group = "junit", name = "junit", version.ref = "junit4" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }
mockk-core = { group = "io.mockk", name = "mockk", version.ref = "mockk-version" }
androidx-test-junit = { module = "androidx.test.ext:junit", version.ref = "testing-androidx-junit" }
androidx-test-espresso-core = { module = "androidx.test.espresso:espresso-core", version.ref = "testing-androidx-espresso" }
```

#### Knox SDK JAR

The module expects a Knox Enterprise SDK JAR file at `libs/knoxsdk_ver38.jar`. This is included as `compileOnly` to avoid conflicts when other modules provide their own SDK variant.

```kotlin
// In knox-licensing/build.gradle.kts
dependencies {
    // Knox SDK - compileOnly so consumers provide their own SDK JAR
    compileOnly(files("libs/knoxsdk_ver38.jar"))

    // Runtime dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.core)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
```

**Note**: The consuming application must provide the Knox SDK JAR either directly or through another module that includes it.

### Device Administrator Requirements

Knox license activation requires elevated permissions. Your application must be registered as one of the following:

1. **Device Administrator (DA)**: Application registered via Device Administration API
   - Requires user consent to enable
   - Suitable for enterprise applications with user interaction
   - Can be disabled by users in Settings

2. **Device Owner (DO)**: Application set as device owner via Device Policy Manager
   - Requires device provisioning or ADB setup
   - Full device management capabilities
   - Cannot be disabled by users

3. **Profile Owner (PO)**: Application set as profile owner for work profiles
   - Manages work profile on personally owned devices
   - Suitable for BYOD scenarios

**Important**: Knox license activation will fail with error code 301 (ERROR_INTERNAL) if the application does not have proper Device Administrator privileges. See the troubleshooting section below for implementation guidance.

**Note**: The library does not include dependencies on specific device detection libraries. Applications can implement their own device detection logic through the `LicenseSelectionStrategy` interface.

## Error Handling

The library provides comprehensive error mapping for Knox SDK error codes:

- `ERROR_NONE`: No error
- `ERROR_INVALID_LICENSE`: Invalid license key
- `ERROR_LICENSE_TERMINATED`: License has been terminated
- `ERROR_LICENSE_EXPIRED`: License has expired
- `ERROR_NETWORK_DISCONNECTED`: Network disconnected
- And many more...

All errors are returned as `LicenseResult.Error` with descriptive messages and original error codes.

## Security Considerations

### License Key Storage and Protection

Knox license keys are sensitive credentials that provide access to enterprise features. Proper security measures are essential for production deployments.

#### Development Environment

1. **local.properties**: Store environment-specific keys (never committed to VCS)
   ```properties
   # Default/fallback license
   knox.license=KNOX000000000000000000000000000000000000000000000000000000000000

   # Environment-specific licenses
   knox.license.development=KNOX111111111111111111111111111111111111111111111111111111111111
   knox.license.production=KNOX222222222222222222222222222222222222222222222222222222222222
   knox.license.staging=KNOX333333333333333333333333333333333333333333333333333333333333
   ```

2. **Environment Variables**: Alternative for CI/CD pipelines
   ```bash
   export KNOX_LICENSE_KEY="KNOX000000000000000000000000000000000000000000000000000000000000"
   ```

#### Production Environment

1. **Encrypted SharedPreferences (Recommended for most apps)**:
   ```kotlin
   private fun getEncryptedPreferences(context: Context): SharedPreferences {
       val masterKey = MasterKey.Builder(context)
           .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
           .build()

       return EncryptedSharedPreferences.create(
           context,
           "knox_secure_prefs",
           masterKey,
           EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
           EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
       )
   }
   ```

2. **Android Keystore (Maximum Security)**:
   ```kotlin
   private fun storeInKeystore(alias: String, data: ByteArray) {
       val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeystore")
       val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias,
           KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
           .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
           .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
           .setUserAuthenticationRequired(false)
           .build()
       keyGenerator.init(keyGenParameterSpec)
       keyGenerator.generateKey()

       // Encrypt and store license key
   }
   ```

3. **Server-side Key Management (Enterprise)**:
   - License keys fetched from secure backend
   - Short-lived tokens with refresh mechanism
   - Network security with certificate pinning
   - Device authentication and authorization

#### Security Best Practices

1. **Code Obfuscation**: Enable ProGuard/R8 in production builds
   ```kotlin
   android {
       buildTypes {
           release {
               isMinifyEnabled = true
               proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
           }
       }
   }
   ```

2. **Certificate Pinning**: For server-side key retrieval
   ```kotlin
   val certificatePinner = CertificatePinner.Builder()
       .add("your-api-domain.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
       .build()
   ```

3. **Root Detection**: Knox licenses may fail on rooted devices
   ```kotlin
   private fun isDeviceSecure(): Boolean {
       // Implement root detection logic
       // Check for common root indicators
       return !isRooted()
   }
   ```

4. **License Validation**: Regular validation in production
   ```kotlin
   // Periodically validate license status
   lifecycleScope.launch {
       val licenseInfo = knoxLicenseHandler.getLicenseInfo()
       if (licenseInfo is LicenseInfo.Valid) {
           // Check expiration, terms, etc.
       }
   }
   ```

### Network Security

When implementing server-side license management:

1. **TLS 1.3**: Use latest TLS version for key exchange
2. **Certificate Pinning**: Pin server certificates to prevent MITM
3. **Request Signing**: Sign API requests with device-specific keys
4. **Token-based Auth**: Use short-lived tokens instead of static keys

### Device Administrator Security

Device Administrator privileges are powerful and require careful handling:

1. **Principle of Least Privilege**: Only request necessary DA permissions
2. **User Consent**: Clearly explain why DA privileges are needed
3. **Secure Storage**: Protect any DA-related configuration data
4. **Audit Logging**: Log DA privilege usage for security monitoring

### Knox Container Considerations

For Knox-enabled devices with container support:

1. **Container Isolation**: License applies to specific container
2. **Cross-container Restrictions**: Licenses don't transfer between containers
3. **Data Leakage**: Prevent license data from crossing container boundaries

**Critical Security Notice**: Never commit license keys to version control, disable debugging in production builds, and regularly rotate license keys in enterprise environments.

## Performance Considerations

### License Activation Performance

Knox license activation involves network communication and cryptographic operations. Consider these performance implications:

#### Startup Performance

1. **Asynchronous Initialization**: Always initialize licenses on background threads
   ```kotlin
   class MyApplication : Application() {
       override fun onCreate() {
           super.onCreate()

           // Launch on IO thread to avoid blocking main thread
           lifecycleScope.launch(Dispatchers.IO) {
               KnoxStartupManager.initializeKnoxLicensing(this@MyApplication)
           }
       }
   }
   ```

2. **Startup Time Impact**: License activation can take 2-5 seconds
   - First activation: 3-5 seconds (network validation)
   - Subsequent startups: 1-2 seconds (cached validation)
   - Offline mode: < 500ms (local validation only)

3. **Progressive Loading**: Don't block UI on license status
   ```kotlin
   @Composable
   fun MyApp() {
       var licenseReady by remember { mutableStateOf(false) }

       LaunchedEffect(Unit) {
           licenseReady = KnoxStartupManager.isKnoxLicenseReady()
       }

       if (licenseReady) {
           FullAppContent()
       } else {
           LoadingScreen()
       }
   }
   ```

#### Memory Usage

1. **Memory Footprint**: Knox SDK adds ~2-5MB to app memory
2. **License Caching**: Cached license data uses ~100KB-500KB
3. **Background Services**: Knox may spawn background services

#### Network Performance

1. **License Validation**: Network calls during activation
   - Initial activation: 1-2 network round trips
   - Periodic validation: Background network usage
   - Offline tolerance: 7-30 days depending on license type

2. **Optimization Strategies**:
   ```kotlin
   // Cache license status to reduce network calls
   private var lastValidationTime = 0L
   private val VALIDATION_INTERVAL = TimeUnit.HOURS.toMillis(24)

   suspend fun checkLicense(): LicenseInfo {
       val now = System.currentTimeMillis()
       if (now - lastValidationTime < VALIDATION_INTERVAL) {
           return getCachedLicenseInfo()
       }

       return knoxLicenseHandler.getLicenseInfo().also {
           lastValidationTime = now
       }
   }
   ```

### Battery Impact

Knox licensing has minimal battery impact when properly implemented:

1. **Activation**: One-time battery cost during initial setup
2. **Background Validation**: Minimal periodic network activity
3. **Best Practices**:
   - Batch license operations
   - Avoid frequent license status checks
   - Use observer patterns instead of polling

### Threading Considerations

1. **Thread Safety**: Knox SDK operations are not thread-safe
   ```kotlin
   class KnoxLicenseRepository {
       private val mutex = Mutex()

       suspend fun activateLicense(): LicenseResult = mutex.withLock {
           knoxLicenseHandler.activate()
       }
   }
   ```

2. **Background Processing**: Always use appropriate dispatchers
   ```kotlin
   // For license operations
   withContext(Dispatchers.IO) {
       knoxLicenseHandler.activate()
   }

   // For UI updates
   withContext(Dispatchers.Main) {
       updateLicenseUI(result)
   }
   ```

### Production Optimization

1. **ProGuard/R8 Optimization**: Knox SDK works with code minification
   ```
   # Keep Knox SDK classes
   -keep class com.samsung.android.knox.** { *; }
   -keep class com.sec.enterprise.knox.** { *; }
   ```

2. **App Bundle Optimization**: Knox features are device-specific
   ```xml
   <!-- Use conditional delivery for Knox features -->
   <conditional-delivery>
       <conditions>
           <device-feature android:name="com.samsung.feature.samsung_experience_mobile" />
       </conditions>
   </conditional-delivery>
   ```

3. **Cold Start Optimization**: Defer non-critical Knox operations
   ```kotlin
   override fun onCreate() {
       super.onCreate()

       // Critical UI setup first
       setContent { MyApp() }

       // Defer Knox initialization
       Handler(Looper.getMainLooper()).postDelayed({
           initializeKnoxLicensing()
       }, 1000)
   }
   ```

### Monitoring and Metrics

Track these key performance metrics in production:

1. **License Activation Time**: Time from start to successful activation
2. **Failure Rate**: Percentage of failed activations
3. **Memory Usage**: Monitor Knox-related memory consumption
4. **Network Usage**: Track license-related network calls

```kotlin
// Example metrics collection
class LicenseMetrics {
    fun recordActivationTime(durationMs: Long) {
        // Send to analytics
    }

    fun recordActivationFailure(errorCode: Int, reason: String) {
        // Track failure patterns
    }
}
```

## Troubleshooting

### Knox License Activation Fails with "Internal Knox error" (Error Code 301)

This error typically indicates missing Device Administrator privileges. To resolve:

1. **Check if your app is a Device Administrator**:
   ```kotlin
   val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
   val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
   val isAdmin = devicePolicyManager.isAdminActive(componentName)
   Log.d("DeviceAdmin", "Is Device Administrator: $isAdmin")
   ```

2. **Implement Device Administrator registration**:
   ```kotlin
   // Create DeviceAdminReceiver
   class MyDeviceAdminReceiver : DeviceAdminReceiver()

   // Register in AndroidManifest.xml
   <receiver android:name=".MyDeviceAdminReceiver"
       android:permission="android.permission.BIND_DEVICE_ADMIN">
       <meta-data android:name="android.app.device_admin"
           android:resource="@xml/device_admin" />
       <intent-filter>
           <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
       </intent-filter>
   </receiver>

   // Request Device Administrator privileges
   val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
   intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
   intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
       "This app requires Device Administrator privileges for Knox license management")
   startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
   ```

3. **Create device_admin.xml**:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <device-admin xmlns:android="http://schemas.android.com/apk/res/android">
       <uses-policies>
           <limit-password />
           <watch-login />
           <reset-password />
           <force-lock />
           <wipe-data />
       </uses-policies>
   </device-admin>
   ```

### Common Error Codes

- **301 (ERROR_INTERNAL)**: Missing Device Administrator privileges or Knox setup issues
- **102 (ERROR_INVALID_LICENSE)**: Invalid license key format or expired license
- **103 (ERROR_INVALID_PACKAGE_NAME)**: License not valid for this application package
- **201 (ERROR_NETWORK_DISCONNECTED)**: Network connectivity required for license validation

### Device Owner Setup (Advanced)

For Device Owner setup via ADB (development/testing):
```bash
adb shell dpm set-device-owner com.yourpackage/.MyDeviceAdminReceiver
```

## License

This library is intended for use with Samsung Knox Enterprise SDK. Ensure you have proper Knox licensing agreements in place.
