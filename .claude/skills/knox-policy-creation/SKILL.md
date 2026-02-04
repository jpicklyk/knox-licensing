---
name: knox-policy-creation
description: Create Knox policies wrapping Samsung Knox SDK APIs. Covers use cases, BooleanStatePolicy, ConfigurableStatePolicy, and state mapping (DIRECT/INVERTED).
---

# Knox Policy Creation

Use this skill when creating new Knox policies that wrap Samsung Knox SDK APIs. This covers the full lifecycle: wrapping Knox SDK APIs in use cases, combining use cases into policy definitions, and properly configuring state mapping.

## Overview

Knox policies follow a layered architecture:

```
Knox SDK API → Use Case → Policy → UI
```

1. **Use Cases**: Wrap individual Knox SDK API calls
2. **Policies**: Combine use cases and handle state mapping
3. **PolicyState**: Represents the domain state for a policy
4. **Configuration**: Transforms between API data and domain state

## When to Use Each Policy Type

### BooleanStatePolicy

Use for **simple on/off toggles** with no additional configuration options.

**Indicators:**
- The feature is purely enabled or disabled
- No additional parameters beyond the toggle state
- Single getter use case + single setter use case

**Examples:**
- Disable 2G Connectivity
- Enable Extra Brightness
- Disable Fast Charging

### ConfigurableStatePolicy

Use for **policies with additional configuration options** beyond just enabled/disabled.

**Indicators:**
- The feature has modes, levels, or choices (e.g., Choice options)
- The feature requires numeric parameters (e.g., band number, SIM slot)
- The feature has multiple toggle options (e.g., selecting which components to disable)
- State needs custom transformation logic between API and domain

**Examples:**
- 5G NR Mode (has mode selection)
- LTE Band Locking (has band number + SIM slot)
- HDM Policy (has multiple component toggles)
- Auto Call Pickup (has mode selection)

---

## State Mapping: DIRECT vs INVERTED

State mapping is **critical** for ensuring the UI displays correctly relative to the Knox API semantics.

### StateMapping.DIRECT

Use when **policy name semantics match API semantics**.

```kotlin
// API: isAlwaysRadioOnEnabled() returns true when enabled
// Policy: "Always Radio On Enabled" - UI shows enabled when API returns true
class AlwaysRadioOnEnabledPolicy : BooleanStatePolicy(stateMapping = StateMapping.DIRECT)
```

Also use DIRECT when the **API already returns the "disabled" state**:

```kotlin
// API: isWirelessChargingDisabled() returns true when disabled
// Policy: "Disable Wireless Charging" - UI shows enabled when API returns true
class DisableWirelessChargingPolicy : BooleanStatePolicy(stateMapping = StateMapping.DIRECT)
```

### StateMapping.INVERTED

Use when **policy name has opposite semantics to API semantics**.

```kotlin
// API: is2gConnectivityEnabled() returns true when 2G is ON
// Policy: "Disable 2G Connectivity" - UI shows enabled when 2G is OFF
// Inversion: policy.isEnabled=true means API.is2gConnectivityEnabled=false
class Disable2GConnectivityPolicy : BooleanStatePolicy(stateMapping = StateMapping.INVERTED)
```

### Decision Matrix

| Policy Name Pattern | API Returns | State Mapping |
|--------------------|-------------|---------------|
| "Enable X" | `isXEnabled()` → true when enabled | DIRECT |
| "Disable X" | `isXEnabled()` → true when enabled | **INVERTED** |
| "Disable X" | `isXDisabled()` → true when disabled | DIRECT |
| "Block X" | `isXAllowed()` → true when allowed | **INVERTED** |
| "Allow X" | `isXAllowed()` → true when allowed | DIRECT |

### How State Mapping Works

In `BooleanStatePolicy`:

```kotlin
private fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
    StateMapping.DIRECT -> enabled
    StateMapping.INVERTED -> !enabled
}

// Applied during getState():
override suspend fun getState(): BooleanPolicyState {
    val apiResult = getEnabled()  // From Knox API
    return defaultValue.copy(isEnabled = mapEnabled(apiResult.data))
}

// Applied during setState():
override suspend fun setState(state: BooleanPolicyState): ApiResult<Unit> =
    setEnabled(mapEnabled(state.isEnabled))  // Mapped before calling Knox API
```

**Key insight**: State mapping is applied in BOTH directions (get and set) to ensure consistency.

---

## Policy Naming Conventions

### Prefer Negative Naming

Policies are typically named in the **negative** (Block, Disable, Restrict) because:
1. The default state is usually "allowed/enabled"
2. Policies **restrict** or **control** behavior
3. Security-focused naming (what is being blocked/disabled)

**Good naming:**
- `Disable2GConnectivityPolicy`
- `DisableFastChargingPolicy`
- `BlockUsbDebuggingPolicy`
- `RestrictCameraAccessPolicy`

**Avoid:**
- `Enable2GConnectivityPolicy` (unless there's a specific reason to enable a disabled default)

### Positive Naming Exceptions

Use positive naming when:
1. The feature is **opt-in** (disabled by default)
2. The policy **enables** a special capability
3. The API semantics naturally align with "enable"

**Examples:**
- `EnableExtraBrightnessPolicy` (extra brightness is off by default)
- `EnableNightVisionModePolicy` (night vision is a special mode)
- `EnableHdmPolicy` (HDM control is an opt-in feature)
- `AlwaysRadioOnEnabledPolicy` (keeping radio on is a special mode)

---

## Creating a BooleanStatePolicy

### Step 1: Create Getter Use Case

```kotlin
// File: Is[Feature]EnabledUseCase.kt or Get[Feature]UseCase.kt
class Is2gConnectivityEnabledUseCase : SuspendingUseCase<Unit, Boolean>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.get2GConnectivityState())
    }
}
```

### Step 2: Create Setter Use Case

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

### Step 3: Create the Policy

```kotlin
// File: Disable2GConnectivityPolicy.kt
@PolicyDefinition(
    title = "Disable 2G Connectivity",
    description = "Enable or disable 2G cellular network connectivity.",
    category = PolicyCategory.Toggle,
    capabilities = [
        PolicyCapability.MODIFIES_RADIO,
        PolicyCapability.REQUIRES_SIM,
        PolicyCapability.AFFECTS_CONNECTIVITY,
        PolicyCapability.SECURITY_SENSITIVE
    ]
)
class Disable2GConnectivityPolicy : BooleanStatePolicy(stateMapping = StateMapping.INVERTED) {
    private val getUseCase = Is2gConnectivityEnabledUseCase()
    private val setUseCase = Set2gConnectivityEnabled()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}
```

### Complete BooleanStatePolicy Template

```kotlin
@PolicyDefinition(
    title = "[Title in Title Case]",
    description = "[Human-readable description of what the policy does]",
    category = PolicyCategory.Toggle,
    capabilities = [
        // Add relevant capabilities
        PolicyCapability.MODIFIES_[SUBSYSTEM],
        // Add requirements if needed
        PolicyCapability.REQUIRES_[HARDWARE],
        // Add impact indicators
        PolicyCapability.AFFECTS_[AREA]
    ]
)
class [PolicyName]Policy : BooleanStatePolicy(stateMapping = StateMapping.[DIRECT|INVERTED]) {
    private val getUseCase = [GetterUseCase]()
    private val setUseCase = [SetterUseCase]()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}
```

---

## Creating a ConfigurableStatePolicy

### Step 1: Create the PolicyState

```kotlin
// File: [Feature]State.kt
data class NrModeState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
    // Policy-specific fields:
    val mode: LteNrMode,
    val simSlotId: Int? = null
) : PolicyState {
    override fun withEnabled(enabled: Boolean): PolicyState = copy(isEnabled = enabled)
    override fun withError(error: ApiError?, exception: Throwable?): PolicyState =
        copy(error = error, exception = exception)
}
```

### Step 2: Create the Configuration

```kotlin
// File: [Feature]Configuration.kt
data class NrModeConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NrModeState, LteNrModeDto> {

    // Transform API data to domain state
    override fun fromApiData(apiData: LteNrModeDto): NrModeState {
        return NrModeState(
            // Derive isEnabled from API data semantically
            isEnabled = (apiData.mode != LteNrMode.EnableBothSaAndNsa),
            mode = apiData.mode,
            simSlotId = apiData.simSlotId
        )
    }

    // Transform domain state to API data
    override fun toApiData(state: NrModeState): LteNrModeDto {
        val dto = LteNrModeDto(state.simSlotId)
        return when (state.isEnabled) {
            true -> dto.copy(mode = state.mode)
            false -> dto.copy(mode = LteNrMode.EnableBothSaAndNsa)  // Default when disabled
        }
    }

    // Convert UI state to domain state
    override fun fromUiState(uiEnabled: Boolean, options: List<ConfigurationOption>): NrModeState {
        val simSlotId = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }?.value ?: 0
        val modeDisplayName = if (uiEnabled) {
            options.filterIsInstance<ConfigurationOption.Choice>()
                .find { it.key == "mode" }?.selected ?: LteNrMode.EnableBothSaAndNsa.displayName
        } else {
            LteNrMode.EnableBothSaAndNsa.displayName
        }
        return NrModeState(
            isEnabled = uiEnabled,
            mode = LteNrMode.fromDisplayName(modeDisplayName),
            simSlotId = simSlotId
        )
    }

    // Generate UI configuration options from domain state
    override fun getConfigurationOptions(state: NrModeState): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = state.simSlotId ?: 0,
            range = 0..2
        ),
        ConfigurationOption.Choice(
            key = "mode",
            label = "Mode",
            options = LteNrMode.values.map { it.displayName },
            selected = state.mode.displayName
        )
    )
}
```

### Step 3: Create the Use Cases

```kotlin
// Getter use case
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

// Setter use case
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

### Step 4: Create the Policy

```kotlin
// File: NrModePolicy.kt
@PolicyDefinition(
    title = "5G NR Mode",
    description = "Configure 5G NR (New Radio) mode settings to control SA and NSA capabilities.",
    category = PolicyCategory.ConfigurableToggle,
    capabilities = [
        PolicyCapability.MODIFIES_RADIO,
        PolicyCapability.REQUIRES_SIM,
        PolicyCapability.AFFECTS_CONNECTIVITY
    ]
)
class NrModePolicy : ConfigurableStatePolicy<NrModeState, LteNrModeDto, NrModeConfiguration>(
    stateMapping = StateMapping.DIRECT
) {
    private val getUseCase = Get5gNrModeUseCase()
    private val setUseCase = Set5gNrModeUseCase()
    override val configuration = NrModeConfiguration(stateMapping = stateMapping)

    override val defaultValue = NrModeState(
        isEnabled = false,
        mode = LteNrMode.EnableBothSaAndNsa
    )

    override suspend fun getState(parameters: PolicyParameters): NrModeState {
        val simSlotId = (parameters as? NrModeParameters)?.simSlotId
        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> configuration.fromApiData(result.data)
            is ApiResult.NotSupported -> defaultValue.copy(isSupported = false)
            is ApiResult.Error -> defaultValue.copy(error = result.apiError, exception = result.exception)
        }
    }

    override suspend fun setState(state: NrModeState): ApiResult<Unit> {
        return setUseCase(configuration.toApiData(state))
    }
}

// Optional: Custom parameters for the policy
data class NrModeParameters(val simSlotId: Int? = null) : PolicyParameters
```

---

## Configuration Option Types

Use the appropriate `ConfigurationOption` type for your policy's configuration:

### Toggle
For boolean sub-options within the policy:

```kotlin
ConfigurationOption.Toggle(
    key = "useRedOverlay",
    label = "Use red overlay?",
    isEnabled = state.useRedOverlay
)
```

### Choice
For enum-like selections:

```kotlin
ConfigurationOption.Choice(
    key = "mode",
    label = "Mode",
    options = listOf("Option A", "Option B", "Option C"),
    selected = state.selectedOption
)
```

### NumberInput
For numeric parameters:

```kotlin
ConfigurationOption.NumberInput(
    key = "simSlotId",
    label = "SIM Slot Id",
    value = state.simSlotId ?: 0,
    range = 0..2  // Optional range constraint
)
```

### TextInput
For string parameters:

```kotlin
ConfigurationOption.TextInput(
    key = "ssid",
    label = "Network SSID",
    value = state.ssid,
    hint = "Enter network name",
    maxLength = 32
)
```

### TextList
For multiple string values:

```kotlin
ConfigurationOption.TextList(
    key = "allowedDomains",
    label = "Allowed Domains",
    values = state.domains,
    hint = "Add domain"
)
```

---

## Policy Capabilities Reference

Choose capabilities that accurately describe what your policy modifies and requires:

### What it Modifies
- `MODIFIES_RADIO` - Cellular/radio settings
- `MODIFIES_WIFI` - Wi-Fi settings
- `MODIFIES_BLUETOOTH` - Bluetooth settings
- `MODIFIES_DISPLAY` - Screen/display settings
- `MODIFIES_AUDIO` - Sound settings
- `MODIFIES_CHARGING` - Charging behavior
- `MODIFIES_CALLING` - Telephony behavior
- `MODIFIES_HARDWARE` - Hardware components
- `MODIFIES_SECURITY` - Security settings
- `MODIFIES_NETWORK` - Network settings
- `MODIFIES_BROWSER` - Browser settings

### Device Requirements
- `REQUIRES_SIM` - SIM card required
- `REQUIRES_HDM` - Hardware Device Mode required
- `REQUIRES_DUAL_SIM` - Dual SIM support required

### Impact Characteristics
- `SECURITY_SENSITIVE` - Affects security
- `AFFECTS_CONNECTIVITY` - May affect network connectivity
- `AFFECTS_BATTERY` - May affect battery life
- `REQUIRES_REBOOT` - Needs reboot to take effect
- `PERSISTENT_ACROSS_REBOOT` - Settings persist after reboot

### Compliance
- `STIG` - Relevant to STIG compliance

---

## Use Case Patterns

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

Add an operator invoke overload for cleaner API:

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

## File Organization

```
knox-[module]/
└── src/main/java/net/sfelabs/knox_[module]/domain/
    ├── policy/
    │   ├── [feature_group]/
    │   │   ├── [Feature]Policy.kt           # The policy class
    │   │   ├── [Feature]State.kt            # PolicyState implementation (ConfigurableStatePolicy only)
    │   │   └── [Feature]Configuration.kt    # PolicyConfiguration (ConfigurableStatePolicy only)
    │   └── [SimpleFeature]Policy.kt         # BooleanStatePolicy (can be single file)
    ├── use_cases/
    │   └── [feature_group]/
    │       ├── Get[Feature]UseCase.kt       # or Is[Feature]EnabledUseCase.kt
    │       └── Set[Feature]UseCase.kt       # or Enable[Feature]UseCase.kt
    └── model/
        └── [Feature]Dto.kt                  # DTO for API data transfer
```

---

## Common Pitfalls

### 1. Wrong State Mapping

**Problem:** UI toggle state is opposite of expected.

**Solution:** Review whether policy name semantics match or oppose API semantics. Use INVERTED when they oppose.

### 2. Missing mapEnabled in Configuration

**Problem:** State mapping not applied in ConfigurableStatePolicy.

**Solution:** Call `mapEnabled()` in `fromApiData()` and `toApiData()` when needed:

```kotlin
override fun fromApiData(apiData: SomeDto): SomeState {
    return SomeState(
        isEnabled = mapEnabled(apiData.rawEnabled),  // Apply mapping
        // ...
    )
}
```

### 3. fromUiState Applying State Mapping

**Problem:** State mapping applied twice (once in UI, once in fromUiState).

**Solution:** `fromUiState` should NOT apply state mapping - the UI state is already in the correct domain form. Only apply mapping when converting to/from API data.

### 4. Incorrect Parameter Naming

**Problem:** Kotlin warning about named argument mismatch.

**Solution:** Always name the parameter `params` in `execute()`:

```kotlin
override suspend fun execute(params: Boolean): ApiResult<Unit>  // Correct
override suspend fun execute(enabled: Boolean): ApiResult<Unit> // Wrong - causes warning
```

### 5. Semantic isEnabled Derivation

**Problem:** `isEnabled` doesn't reflect meaningful state for ConfigurableStatePolicy.

**Solution:** Derive `isEnabled` from the actual semantic meaning:

```kotlin
// Good: isEnabled derived from mode
isEnabled = (apiData.mode != AutoCallPickupMode.Disable)

// Good: isEnabled derived from non-zero value
isEnabled = (apiData.policyMask != 0)

// Bad: Always true/false regardless of actual state
isEnabled = true
```

---

## Checklist for New Policies

- [ ] Determine policy type (BooleanStatePolicy vs ConfigurableStatePolicy)
- [ ] Determine state mapping (DIRECT vs INVERTED) based on naming semantics
- [ ] Create getter use case
- [ ] Create setter use case (may need enable + disable use cases for some APIs)
- [ ] For ConfigurableStatePolicy: Create PolicyState data class
- [ ] For ConfigurableStatePolicy: Create PolicyConfiguration class
- [ ] Create policy class with @PolicyDefinition annotation
- [ ] Set appropriate capabilities
- [ ] Set correct PolicyCategory (Toggle vs ConfigurableToggle)
- [ ] Test state mapping works correctly in both directions
- [ ] Verify UI displays expected state
