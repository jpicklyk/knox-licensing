# Knox Licensing for Android

A reusable Android library for Samsung Knox Enterprise License Management that provides a clean, coroutine-based API for license activation, deactivation, and monitoring.

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
        // Your production device detection logic here
        return Build.MODEL.contains("PROD") || isProductionBuild()
    }

    private fun isEnterpriseDevice(): Boolean {
        // Your enterprise device detection logic here
        return hasEnterpriseFeatures()
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

The library provides `KnoxStartupManager` for convenient initialization during app startup:

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
    fun provideKnoxLicenseHandler(
        @ApplicationContext context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy
    ): KnoxLicenseHandler {
        // Option 1: Use knox-licensing module's BuildConfig
        return KnoxLicenseFactory.create(context, licenseSelectionStrategy)

        // Option 2: Use app's BuildConfig (recommended for multi-module projects)
        // return KnoxLicenseFactory.create(
        //     context = context,
        //     licenseSelectionStrategy = licenseSelectionStrategy,
        //     defaultKey = BuildConfig.KNOX_LICENSE_KEY,
        //     namedKeysArray = BuildConfig.KNOX_LICENSE_KEYS
        // )
    }
}

// Usage in your components
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var knoxLicenseHandler: KnoxLicenseHandler

    // Use knoxLicenseHandler...
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

### License Key Storage

1. **BuildConfig (Development)**: Suitable for development and testing
2. **Encrypted SharedPreferences**: For production apps with runtime key configuration
3. **Server-side Validation**: Most secure approach with server-provided keys
4. **Keystore Integration**: For maximum security with hardware-backed storage

**Never commit license keys to version control.** Always use `local.properties` or environment variables.

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
