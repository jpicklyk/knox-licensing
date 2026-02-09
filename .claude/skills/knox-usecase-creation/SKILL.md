---
name: knox-usecase-creation
description: Create Knox use cases that wrap Samsung Knox SDK API calls using the SuspendingUseCase pattern. Use when creating new use cases, wrapping Knox SDK APIs, implementing getter/setter patterns, or when the knox-policy-creation skill needs use cases built first.
---

# Knox Use Case Creation

Use this skill when wrapping Samsung Knox SDK API calls in use case classes. Use cases are the foundational layer that policies build on.

## Architecture

```
Knox SDK API → Use Case → Policy → UI
```

Use cases wrap individual Knox SDK API calls and return `ApiResult<R>`.

## Use Case Base Class

All use cases extend `SuspendingUseCase<P, R>` where:
- `P` = parameter type (use `Unit` for no parameters)
- `R` = return type

**Critical**: Always name the parameter `params` in `execute()` to match the base class:

```kotlin
override suspend fun execute(params: Boolean): ApiResult<Unit>  // Correct
override suspend fun execute(enabled: Boolean): ApiResult<Unit> // Causes warning
```

## Parameter Patterns

### No Parameters

```kotlin
class GetBrightnessUseCase : SuspendingUseCase<Unit, Int>() {
    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(displayManager.brightness)
    }
}
// Usage: getBrightnessUseCase()
```

### Single Primitive Parameter

```kotlin
class SetBrightnessUseCase : SuspendingUseCase<Int, Unit>() {
    override suspend fun execute(params: Int): ApiResult<Unit> {
        displayManager.setBrightness(params)
        return ApiResult.Success(Unit)
    }
}
// Usage: setBrightnessUseCase(50)
```

### Single DTO Parameter

```kotlin
class SetNrModeUseCase : SuspendingUseCase<LteNrModeDto, Unit>() {
    override suspend fun execute(params: LteNrModeDto): ApiResult<Unit> {
        systemManager.set5gNrModeState(params.mode.value)
        return ApiResult.Success(Unit)
    }
}
// Usage: setNrModeUseCase(LteNrModeDto(mode = LteNrMode.DisableSa))
```

### Multiple Parameters (Nested Params Class)

```kotlin
class SetHdmPolicyUseCase : SuspendingUseCase<SetHdmPolicyUseCase.Params, Unit>() {
    data class Params(val policyMask: Int, val persist: Boolean)

    override suspend fun execute(params: Params): ApiResult<Unit> {
        hdmManager.setHdmPolicy(params.policyMask, params.persist)
        return ApiResult.Success(Unit)
    }
}
// Usage: setHdmPolicyUseCase(Params(policyMask = 0xFF, persist = true))
```

### Convenience Overload Pattern

Add an operator invoke overload for cleaner API when using a Params class:

```kotlin
class Set2gConnectivityEnabled : SuspendingUseCase<Set2gConnectivityEnabled.Params, Unit>() {
    class Params(val enabled: Boolean)

    // Convenience overload
    suspend operator fun invoke(enabled: Boolean): ApiResult<Unit> = invoke(Params(enabled))

    override suspend fun execute(params: Params): ApiResult<Unit> {
        // Implementation
    }
}
// Usage: set2gConnectivityEnabled(true)  // Cleaner than set2gConnectivityEnabled(Params(true))
```

---

## Getter/Setter Pair Pattern

Most Knox features need a getter and setter use case pair.

### Boolean Getter (Is/Get pattern)

```kotlin
// File: Is[Feature]EnabledUseCase.kt or Get[Feature]UseCase.kt
class Is2gConnectivityEnabledUseCase : SuspendingUseCase<Unit, Boolean>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.get2GConnectivityState())
    }
}
```

### Boolean Setter

```kotlin
// File: Set[Feature]EnabledUseCase.kt or Enable[Feature]UseCase.kt
class Set2gConnectivityEnabled : SuspendingUseCase<Boolean, Unit>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when (systemManager.set2GConnectivityState(params)) {
            CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
            else -> ApiResult.Error(
                DefaultApiError.UnexpectedError("The operation failed for an unknown reason.")
            )
        }
    }
}
```

### DTO Getter (Complex return types)

```kotlin
class Get5gNrModeUseCase : SuspendingUseCase<Int?, LteNrModeDto>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Int?): ApiResult<LteNrModeDto> {
        val result = when (params) {
            null -> systemManager.get5gNrModeState()
            else -> systemManager.get5gNrModeStatePerSimSlot(params)
        }
        return if (result == CustomDeviceManager.ERROR_FAIL) {
            ApiResult.Error(DefaultApiError.UnexpectedError("Getting 5gNrModeState error"))
        } else {
            ApiResult.Success(LteNrModeDto(params, LteNrMode(result)))
        }
    }
}
```

### DTO Setter

```kotlin
class Set5gNrModeUseCase : SuspendingUseCase<LteNrModeDto, Unit>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: LteNrModeDto): ApiResult<Unit> {
        val result = systemManager.set5gNrModeState(params.mode.value)
        return if (result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError("Setting 5gNrModeState error"))
        }
    }
}
```

---

## Error Handling Patterns

### Integer Return Code APIs

```kotlin
return when (systemManager.someOperation(params)) {
    CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
    else -> ApiResult.Error(
        DefaultApiError.UnexpectedError("The operation failed for an unknown reason.")
    )
}
```

### Error Code Comparison APIs

```kotlin
return if (result == CustomDeviceManager.ERROR_FAIL) {
    ApiResult.Error(DefaultApiError.UnexpectedError("Descriptive error message"))
} else {
    ApiResult.Success(mappedResult)
}
```

---

## File Organization

```
knox-[module]/
└── src/main/java/net/sfelabs/knox_[module]/domain/
    ├── use_cases/
    │   └── [feature_group]/
    │       ├── Get[Feature]UseCase.kt       # or Is[Feature]EnabledUseCase.kt
    │       └── Set[Feature]UseCase.kt       # or Enable[Feature]UseCase.kt
    └── model/
        └── [Feature]Dto.kt                  # DTO for API data transfer
```

## Naming Conventions

- Getter: `Is[Feature]EnabledUseCase` (boolean) or `Get[Feature]UseCase` (value/DTO)
- Setter: `Set[Feature]EnabledUseCase` (boolean) or `Set[Feature]UseCase` (value/DTO)
- Alternative setter: `Enable[Feature]UseCase` when API has separate enable/disable calls
- DTO: `[Feature]Dto` for complex data transfer objects

## Checklist

- [ ] Choose correct parameter pattern (Unit, primitive, DTO, or nested Params)
- [ ] Name execute parameter `params` (not a descriptive name)
- [ ] Use `ApiResult.Success` / `ApiResult.Error` for return values
- [ ] Handle error codes from Knox SDK calls
- [ ] Place in correct `use_cases/[feature_group]/` directory
- [ ] Create DTO if returning complex data
