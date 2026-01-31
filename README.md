# Knox Licensing for Android

A reusable Android library for Samsung Knox Enterprise License Management that provides a clean, coroutine-based API for license activation, deactivation, and monitoring.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
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
  - [Device Administrator Requirements](#device-administrator-requirements)
- [Error Handling](#error-handling)
- [Security Considerations](#security-considerations)
- [Performance Considerations](#performance-considerations)
- [Real-World Examples](#real-world-examples)
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

### Using BuildConfig (Recommended)

Create a gradle convention plugin or add directly to your `build.gradle.kts`:

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

## Real-World Implementation Examples

### Example 1: Build Variant-Based License Selection

Realistic implementation that selects between development and production licenses based on build variant:

```kotlin
// 1. Build Variant-Based License Selection Strategy
class BuildVariantLicenseStrategy : LicenseSelectionStrategy {
    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        return when {
            BuildConfig.DEBUG -> {
                // Development builds use development license
                availableKeys["development"]
                    ?: availableKeys["dev"]
                    ?: defaultKey
            }
            BuildConfig.BUILD_TYPE == "release" -> {
                // Production builds use production license
                availableKeys["production"]
                    ?: availableKeys["prod"]
                    ?: defaultKey
            }
            BuildConfig.BUILD_TYPE == "staging" -> {
                // Staging builds use staging license or fallback to development
                availableKeys["staging"]
                    ?: availableKeys["development"]
                    ?: defaultKey
            }
            else -> {
                // Unknown build type, use default
                Log.w("LicenseStrategy", "Unknown build type: ${BuildConfig.BUILD_TYPE}, using default license")
                defaultKey
            }
        }
    }
}

// Alternative: Environment-Aware Strategy with Device Type Support
class EnvironmentAwareLicenseStrategy : LicenseSelectionStrategy {
    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        // First determine environment
        val environment = when {
            BuildConfig.DEBUG -> "development"
            BuildConfig.BUILD_TYPE == "release" -> "production"
            BuildConfig.BUILD_TYPE == "staging" -> "staging"
            else -> "unknown"
        }

        // Then consider device characteristics for production deployments
        val deviceSuffix = if (environment == "production") {
            when {
                isTabletDevice() -> "_tablet"
                isRuggedDevice() -> "_rugged"
                else -> ""
            }
        } else {
            "" // Dev/staging use standard keys without device differentiation
        }

        val preferredKey = "$environment$deviceSuffix"

        return availableKeys[preferredKey]
            ?: availableKeys[environment]
            ?: defaultKey.also {
                Log.w("LicenseStrategy", "No license found for $preferredKey or $environment, using default")
            }
    }

    private fun isTabletDevice(): Boolean {
        val metrics = Resources.getSystem().displayMetrics
        val widthDp = metrics.widthPixels / metrics.density
        return widthDp >= 600
    }

    private fun isRuggedDevice(): Boolean {
        val model = Build.MODEL.lowercase()
        return model.contains("xcover") || model.contains("galaxy tab active")
    }
}

// 2. Application Class with Build Variant License Strategy
@HiltAndroidApp
class MyApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // Check Device Administrator status first
        val deviceAdminManager = DeviceAdminManager(this)

        if (!deviceAdminManager.hasElevatedPrivileges()) {
            Log.w(TAG, "Knox licensing requires Device Administrator privileges")
            // Continue with app startup but show admin requirement in UI
        }

        // Initialize Knox licensing asynchronously with build variant strategy
        applicationScope.launch(Dispatchers.IO) {
            try {
                val strategy = BuildVariantLicenseStrategy()
                val result = KnoxStartupManager.initializeKnoxLicensing(
                    this@MyApplication,
                    strategy,
                    BuildConfig.KNOX_LICENSE_KEY,
                    BuildConfig.KNOX_LICENSE_KEYS
                )

                when (result) {
                    is LicenseStartupResult.ActivatedNow -> {
                        val environment = if (BuildConfig.DEBUG) "development" else "production"
                        Log.i(TAG, "Knox license activated for $environment environment")
                        enableKnoxFeatures()
                    }
                    is LicenseStartupResult.AlreadyActivated -> {
                        Log.d(TAG, "Knox license already active")
                        enableKnoxFeatures()
                    }
                    is LicenseStartupResult.ActivationFailed -> {
                        Log.e(TAG, "Knox license activation failed: ${result.reason}")
                        if (BuildConfig.DEBUG) {
                            Log.w(TAG, "Development build - Knox features will be limited")
                        }
                        enableLimitedMode()
                    }
                    else -> {
                        Log.w(TAG, "Knox license status unknown")
                        enableLimitedMode()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Knox initialization", e)
                enableLimitedMode()
            }
        }
    }

    private fun enableKnoxFeatures() {
        // Enable Knox-dependent features
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean("knox_features_enabled", true)
            .apply()
    }

    private fun enableLimitedMode() {
        // Disable Knox-dependent features, app runs with basic functionality
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean("knox_features_enabled", false)
            .apply()
    }

    companion object {
        private const val TAG = "MyApplication"
    }
}

// 3. Supporting Build Configuration
// In your app/build.gradle.kts, ensure you have proper license key configuration:

/*
android {
    defaultConfig {
        buildConfigField("String", "KNOX_LICENSE_KEY", getDefaultLicenseKey())
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", getNamedLicenseKeys())
    }
}

fun getNamedLicenseKeys(): String {
    val localPropsFile = File(project.rootDir, "local.properties")
    val localProps = Properties()
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { localProps.load(it) }
    }

    val keys = mutableListOf<String>()

    // Add development license
    localProps.getProperty("knox.license.development")?.let { key ->
        keys.add("development:$key")
    }

    // Add production license
    localProps.getProperty("knox.license.production")?.let { key ->
        keys.add("production:$key")
    }

    // Add staging license if available
    localProps.getProperty("knox.license.staging")?.let { key ->
        keys.add("staging:$key")
    }

    return "new String[]{${keys.joinToString(", ") { "\"$it\"" }}}"
}
*/

// 4. Example local.properties file:
/*
# Knox License Keys for Different Environments
knox.license=KNOX000000000000000000000000000000000000000000000000000000000000  # Default/fallback
knox.license.development=KNOX111111111111111111111111111111111111111111111111111111111111
knox.license.production=KNOX222222222222222222222222222222222222222222222222222222222222
knox.license.staging=KNOX333333333333333333333333333333333333333333333333333333333333

# Optional: Device-specific production licenses
knox.license.production_tablet=KNOX444444444444444444444444444444444444444444444444444444444444
knox.license.production_rugged=KNOX555555555555555555555555555555555555555555555555555555555555
*/

// 5. Device Administrator Setup
class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device Administrator enabled")

        // Trigger Knox license initialization now that we have DA privileges
        CoroutineScope(Dispatchers.IO).launch {
            KnoxStartupManager.reset()
            val strategy = BuildVariantLicenseStrategy()
            KnoxStartupManager.initializeKnoxLicensing(
                context,
                strategy,
                BuildConfig.KNOX_LICENSE_KEY,
                BuildConfig.KNOX_LICENSE_KEYS
            )
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.w(TAG, "Device Administrator disabled - Knox features will be limited")
    }

    companion object {
        private const val TAG = "MyDeviceAdminReceiver"
    }
}

// 6. UI Integration with Build Variant Awareness
@Composable
fun MainScreen(
    deviceAdminManager: DeviceAdminManager = hiltViewModel()
) {
    var licenseStatus by remember { mutableStateOf<LicenseInfo?>(null) }
    var knoxFeaturesEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Check Knox features availability
        knoxFeaturesEnabled = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("knox_features_enabled", false)

        // Get license status if available
        if (KnoxStartupManager.isKnoxLicenseReady()) {
            try {
                val handler = KnoxLicenseFactory.create(context, BuildVariantLicenseStrategy())
                licenseStatus = handler.getLicenseInfo()
            } catch (e: Exception) {
                Log.e("MainScreen", "Error getting license info", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Build Environment Indicator
        BuildEnvironmentCard()

        // Device Administrator Status
        AdminStatusCard(deviceAdminManager = deviceAdminManager)

        // Knox License Status
        LicenseStatusCard(licenseStatus = licenseStatus)

        // Knox Features
        if (knoxFeaturesEnabled) {
            KnoxFeaturesList()
        } else {
            LimitedModeCard()
        }
    }
}

@Composable
fun BuildEnvironmentCard() {
    val environment = if (BuildConfig.DEBUG) "Development" else "Production"
    val buildType = BuildConfig.BUILD_TYPE

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (BuildConfig.DEBUG)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Build Environment",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Environment: $environment")
            Text(text = "Build Type: $buildType")
            if (BuildConfig.DEBUG) {
                Text(
                    text = "⚠ Development build - using development Knox license",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AdminStatusCard(deviceAdminManager: DeviceAdminManager) {
    val hasPrivileges = deviceAdminManager.hasElevatedPrivileges()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasPrivileges)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Device Administration Status",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (hasPrivileges)
                    "✓ Device Administrator enabled"
                else
                    "⚠ Knox licensing requires Device Administrator"
            )
        }
    }
}
```

### Example 2: Manufacturing Device Provisioning

Implementation for a manufacturing environment where devices are provisioned with Knox licenses:

```kotlin
// Manufacturing License Provisioning Strategy
class ManufacturingProvisioningStrategy : LicenseSelectionStrategy {
    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        // Read device-specific license from secure storage or server
        val deviceSerial = Build.getSerial()
        val provisioningKey = getProvisioningKey(deviceSerial)

        return when {
            provisioningKey != null -> provisioningKey
            availableKeys.containsKey("manufacturing") -> availableKeys["manufacturing"]!!
            else -> defaultKey
        }
    }

    private fun getProvisioningKey(serial: String): String? {
        // In real implementation, this would query a secure provisioning server
        // or read from encrypted local storage set during manufacturing
        return ProvisioningStorage.getLicenseForSerial(serial)
    }
}

// Provisioning Service for Manufacturing
class DeviceProvisioningService @Inject constructor(
    private val context: Context,
    private val deviceAdminManager: DeviceAdminManager
) {

    suspend fun provisionDevice(
        deviceSerial: String,
        licenseKey: String,
        configurationProfile: DeviceConfiguration
    ): ProvisioningResult {
        return try {
            // Step 1: Ensure Device Administrator privileges
            if (!deviceAdminManager.hasElevatedPrivileges()) {
                return ProvisioningResult.Error("Device Administrator privileges required")
            }

            // Step 2: Store license key securely
            ProvisioningStorage.storeLicenseKey(deviceSerial, licenseKey)

            // Step 3: Initialize Knox with manufacturing strategy
            val strategy = ManufacturingProvisioningStrategy()
            val result = KnoxStartupManager.initializeKnoxLicensing(
                context,
                strategy,
                licenseKey,
                arrayOf("manufacturing:$licenseKey")
            )

            // Step 4: Apply device configuration
            when (result) {
                is LicenseStartupResult.ActivatedNow,
                is LicenseStartupResult.AlreadyActivated -> {
                    applyDeviceConfiguration(configurationProfile)
                    ProvisioningResult.Success(deviceSerial)
                }
                else -> {
                    ProvisioningResult.Error("Knox license activation failed: $result")
                }
            }
        } catch (e: Exception) {
            ProvisioningResult.Error("Provisioning failed: ${e.message}")
        }
    }

    private suspend fun applyDeviceConfiguration(config: DeviceConfiguration) {
        // Apply enterprise policies, network settings, etc.
        // This would use Knox APIs to configure the device
    }
}

sealed class ProvisioningResult {
    data class Success(val deviceSerial: String) : ProvisioningResult()
    data class Error(val message: String) : ProvisioningResult()
}
```

### Example 3: Multi-Tenant SaaS Application

Implementation for a SaaS application that serves multiple enterprise customers:

```kotlin
// Multi-tenant License Strategy
class MultiTenantLicenseStrategy(
    private val tenantManager: TenantManager
) : LicenseSelectionStrategy {

    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        val currentTenant = tenantManager.getCurrentTenant()

        return when {
            currentTenant != null -> {
                // Use tenant-specific license if available
                availableKeys["tenant_${currentTenant.id}"]
                    ?: availableKeys["tenant_default"]
                    ?: defaultKey
            }
            else -> defaultKey
        }
    }
}

// Tenant Management
@Singleton
class TenantManager @Inject constructor(
    private val context: Context
) {
    private var currentTenant: Tenant? = null

    suspend fun switchTenant(tenantId: String): Result<Unit> {
        return try {
            // Deactivate current license if any
            if (currentTenant != null) {
                val handler = KnoxLicenseFactory.create(context, MultiTenantLicenseStrategy(this))
                handler.deactivate()
            }

            // Load new tenant configuration
            currentTenant = loadTenantConfiguration(tenantId)

            // Initialize Knox for new tenant
            val strategy = MultiTenantLicenseStrategy(this)
            val result = KnoxStartupManager.initializeKnoxLicensing(
                context,
                strategy,
                BuildConfig.KNOX_LICENSE_KEY,
                BuildConfig.KNOX_LICENSE_KEYS
            )

            when (result) {
                is LicenseStartupResult.ActivatedNow,
                is LicenseStartupResult.AlreadyActivated -> {
                    Result.success(Unit)
                }
                else -> {
                    Result.failure(Exception("Failed to activate license for tenant $tenantId"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentTenant(): Tenant? = currentTenant

    private suspend fun loadTenantConfiguration(tenantId: String): Tenant {
        // Load tenant configuration from secure storage or server
        return TenantStorage.getTenant(tenantId)
    }
}

// SaaS Application with Tenant Switching
@Composable
fun SaaSMainScreen(
    tenantManager: TenantManager = hiltViewModel()
) {
    var currentTenant by remember { mutableStateOf(tenantManager.getCurrentTenant()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentTenant = tenantManager.getCurrentTenant()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tenant Selector
        TenantSelector(
            currentTenant = currentTenant,
            onTenantChange = { newTenantId ->
                isLoading = true
                error = null

                CoroutineScope(Dispatchers.IO).launch {
                    tenantManager.switchTenant(newTenantId)
                        .onSuccess {
                            currentTenant = tenantManager.getCurrentTenant()
                            isLoading = false
                        }
                        .onFailure { e ->
                            error = e.message
                            isLoading = false
                        }
                }
            }
        )

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Main content based on current tenant
            currentTenant?.let { tenant ->
                TenantSpecificContent(tenant = tenant)
            } ?: run {
                Text("No tenant selected")
            }
        }

        // Error display
        error?.let { errorMessage ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
```

These examples demonstrate:

1. **Enterprise Device Management**: Complete Knox integration with Device Administrator setup
2. **Manufacturing Provisioning**: Device-specific license provisioning in manufacturing environments
3. **Multi-Tenant SaaS**: Dynamic license switching for multi-tenant applications

Each example includes proper error handling, UI integration, and follows Android development best practices while leveraging the Knox licensing library effectively.

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
