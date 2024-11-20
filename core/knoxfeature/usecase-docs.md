# Use Case Generation System

## Overview
This documentation covers the Use Case Generation System, which provides a framework for generating both standard use cases and feature-toggled use cases. The system uses KSP (Kotlin Symbol Processing) for code generation and supports metrics tracking and coroutine integration.

## Table of Contents
- [Setup](#setup)
- [Core Components](#core-components)
- [Use Cases](#use-cases)
- [Feature Use Cases](#feature-use-cases)
- [Metrics](#metrics)
- [Code Generation](#code-generation)

## Setup

### Project Structure
```
your-project/
├── core/
│   ├── common/         # Base interfaces and utilities
│   ├── feature/        # Feature toggle system
│   └── feature-processor/  # KSP processors
└── settings.gradle.kts
```

### Gradle Configuration

```kotlin
// settings.gradle.kts
include(
    ":core:common",
    ":core:feature",
    ":core:feature-processor"
)

// core/common/build.gradle.kts
plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

// core/feature/build.gradle.kts
plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":core:common"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    ksp(project(":core:feature-processor"))
}

// core/feature-processor/build.gradle.kts
plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
}
```

## Core Components

### Base Use Case
```kotlin
interface ApiUseCase<in P, out R : Any> {
    suspend operator fun invoke(params: P): ApiResult<R>
}

abstract class CoroutineApiUseCase<in P, out R : Any>(
    private val dispatcher: CoroutineDispatcher? = null
) : ApiUseCase<P, R>
```

### Feature Use Case
```kotlin
interface FeatureUseCase<T, P, R : Any> : FeatureHandler<T>, ApiUseCase<P, R>

abstract class CoroutineFeatureUseCase<T, P, R : Any>(
    dispatcher: CoroutineDispatcher? = null
) : CoroutineApiUseCase<P, R>(dispatcher), FeatureUseCase<T, P, R>
```

## Use Cases

### Creating a Standard Use Case
```kotlin
@GeneratedUseCase(
    name = "GetUser",
    withMetrics = true,
    defaultBlocking = true
)
class GetUserUseCase(
    private val api: UserApi,
    dispatcher: CoroutineDispatcher? = null
) : CoroutineApiUseCase<String, User>(dispatcher)
```

### Creating a Feature Use Case when needing policy like restrictions that get turned on/off
```kotlin
@GeneratedFeatureUseCase(
    feature = UserFeature::class,
    category = FeatureCategory.PRODUCTION,
    defaultBlocking = true
)
class GetUserFeatureUseCase(
    private val api: UserApi,
    dispatcher: CoroutineDispatcher? = null
) : CoroutineFeatureUseCase<Boolean, String, User>(dispatcher)
```

## Factories and Builders

### Use Case Factory
```kotlin
// Register a use case
UseCaseFactory.register(
    "GetUser",
    GetUserUseCase.Builder()
        .withApi(userApi)
        .withDispatcher(Dispatchers.IO)
        .build()
)

// Get a use case
val useCase = UseCaseFactory.getUseCase<String, User>("GetUser")
```

### Feature Use Case Factory
```kotlin
// Register a feature use case
FeatureUseCaseFactory.register(
    UserFeature::class,
    GetUserFeatureUseCase.Builder()
        .withApi(userApi)
        .withDispatcher(Dispatchers.IO)
        .build()
)

// Get a feature use case
val featureUseCase = FeatureUseCaseFactory.getUseCase<Boolean, String, User>(UserFeature)
```

## Metrics
The system automatically tracks:
- Execution duration
- Success count
- Error count

```kotlin
val metrics = useCase.metrics.getMetrics("GetUser")
println("Average duration: ${metrics.averageDurationMs}ms")
println("Success count: ${metrics.successCount}")
println("Error count: ${metrics.errorCount}")
```

## Code Generation

### Annotations
```kotlin
@Target(AnnotationTarget.CLASS)
annotation class GeneratedUseCase(
    val name: String,
    val withMetrics: Boolean = true,
    val defaultBlocking: Boolean = false
)

@Target(AnnotationTarget.CLASS)
annotation class GeneratedFeatureUseCase(
    val feature: KClass<out FeatureKey<*>>,
    val category: FeatureCategory = FeatureCategory.PRODUCTION,
    val defaultBlocking: Boolean = false
)

@Target(AnnotationTarget.FUNCTION)
annotation class Blocking(
    val timeoutMs: Long = 5000
)
```

### Generated Code Example
The system will generate implementations including:
- Proper error handling
- Metrics collection (if enabled)
- Coroutine context switching (if blocking)
- Feature state handling (for feature use cases)

## Best Practices

1. **Use Case Naming**
   - Use descriptive names that indicate the action
   - Follow the pattern: `{Action}{Entity}UseCase`

2. **Error Handling**
   - Let the base classes handle common errors
   - Override `mapError` for custom error mapping

3. **Coroutines**
   - Use appropriate dispatchers for blocking operations
   - Consider timeout values for blocking operations

4. **Features**
   - Group related features using sealed classes
   - Use meaningful feature names
   - Document feature purposes and impact

5. **Testing**
   - Test both enabled and disabled states for features
   - Verify metrics collection
   - Test timeout behavior for blocking operations

## Example Implementation

```kotlin
// Feature definition
sealed class UserFeatures<T>(override val featureName: String) : FeatureKey<T> {
    data object EnhancedProfile : UserFeatures<Boolean>("enhanced_profile")
}

// Use case implementation
@GeneratedFeatureUseCase(
    feature = UserFeatures.EnhancedProfile::class,
    defaultBlocking = true
)
class GetUserProfileUseCase(
    private val api: UserApi,
    dispatcher: CoroutineDispatcher? = null
) : CoroutineFeatureUseCase<Boolean, String, UserProfile>(dispatcher) {
    
    override suspend fun executeEnabled(
        params: String, 
        state: FeatureState<Boolean>
    ): ApiResult<UserProfile> =
        api.getEnhancedProfile(params)
    
    override suspend fun executeDisabled(
        params: String, 
        state: FeatureState<Boolean>
    ): ApiResult<UserProfile> =
        api.getBasicProfile(params)
}

// Usage
suspend fun getProfile(userId: String) {
    val useCase = FeatureUseCaseFactory.getUseCase<Boolean, String, UserProfile>(
        UserFeatures.EnhancedProfile
    )
    when (val result = useCase(userId)) {
        is ApiResult.Success -> handleProfile(result.data)
        is ApiResult.Error -> handleError(result.error)
        is ApiResult.NotSupported -> handleNotSupported()
    }
}
```
