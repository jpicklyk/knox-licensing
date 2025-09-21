# Knox Licensing for Android

A reusable Android library for Samsung Knox Enterprise License Management that provides a clean, coroutine-based API for license activation, deactivation, and monitoring.

## Features

- **Clean Architecture**: Separation of domain and data layers
- **Coroutines Support**: Async/await license operations with Flow-based state monitoring
- **Named License Keys**: Support for multiple named license configurations
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

class MainActivity : AppCompatActivity() {
    private lateinit var knoxLicenseHandler: KnoxLicenseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create handler from BuildConfig (requires BuildConfig.KNOX_LICENSE_KEY)
        knoxLicenseHandler = KnoxLicenseFactory.createFromBuildConfig(this)

        // Or create with explicit keys
        knoxLicenseHandler = KnoxLicenseFactory.createWithKeys(
            context = this,
            defaultKey = "KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX",
            namedKeys = mapOf(
                "tactical" to "KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY",
                "enterprise" to "KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ"
            )
        )
    }
}
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
    val result = knoxLicenseHandler.activate("tactical")
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
if (knoxLicenseHandler.hasLicense("tactical")) {
    // Tactical license is configured
}
```

## Configuration

### Using BuildConfig (Recommended)

Create a gradle convention plugin or add directly to your `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "KNOX_LICENSE_KEY", "\"${project.findProperty("knox.license") ?: ""}\"")
        buildConfigField("String[]", "KNOX_LICENSE_KEYS", "${getNamedLicenseKeys()}")
    }

    buildFeatures {
        buildConfig = true
    }
}

fun getNamedLicenseKeys(): String {
    val keys = mutableListOf<String>()

    // Add named keys from properties
    project.findProperty("knox.license.tactical")?.let { key ->
        keys.add("\"tactical:$key\"")
    }
    project.findProperty("knox.license.enterprise")?.let { key ->
        keys.add("\"enterprise:$key\"")
    }

    return if (keys.isEmpty()) "{}" else "{${keys.joinToString(", ")}}"
}
```

Add to your `local.properties`:

```properties
knox.license=KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX
knox.license.tactical=KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY
knox.license.enterprise=KLM06-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ-ZZZZZ
```

### Manual Configuration

```kotlin
val licenseConfiguration = LicenseConfiguration(
    defaultKey = "KLM06-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX",
    namedKeys = mapOf(
        "tactical" to "KLM06-YYYYY-YYYYY-YYYYY-YYYYY-YYYYY",
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
    fun provideKnoxLicenseHandler(@ApplicationContext context: Context): KnoxLicenseHandler {
        return KnoxLicenseFactory.createFromBuildConfig(context)
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
    single<KnoxLicenseHandler> {
        KnoxLicenseFactory.createFromBuildConfig(androidContext())
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
- Device Owner or Admin privileges for license operations

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

## License

This library is intended for use with Samsung Knox Enterprise SDK. Ensure you have proper Knox licensing agreements in place.
