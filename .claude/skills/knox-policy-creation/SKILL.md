---
name: knox-policy-creation
description: Create Knox policies using BooleanStatePolicy or ConfigurableStatePolicy patterns with proper state mapping (DIRECT/INVERTED). Use when creating new policy classes, choosing between policy types, configuring state mapping, setting up PolicyDefinition annotations, or building ConfigurableStatePolicy with PolicyState and PolicyConfiguration.
---

# Knox Policy Creation

Use this skill when creating new Knox policies that combine use cases into policy definitions with state mapping and UI configuration.

**Prerequisites**: Use cases should be created first. See the `/knox-usecase-creation` skill for wrapping Knox SDK APIs in use case classes.

## Overview

Knox policies follow a layered architecture:

```
Knox SDK API → Use Case → Policy → UI
```

1. **Use Cases**: Wrap individual Knox SDK API calls (see `/knox-usecase-creation`)
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

**Examples:** Disable 2G Connectivity, Enable Extra Brightness, Disable Fast Charging

### ConfigurableStatePolicy

Use for **policies with additional configuration options** beyond just enabled/disabled.

**Indicators:**
- The feature has modes, levels, or choices (e.g., Choice options)
- The feature requires numeric parameters (e.g., band number, SIM slot)
- The feature has multiple toggle options (e.g., selecting which components to disable)
- State needs custom transformation logic between API and domain

**Examples:** 5G NR Mode (mode selection), LTE Band Locking (band number + SIM slot), HDM Policy (multiple component toggles)

---

## State Mapping: DIRECT vs INVERTED

State mapping ensures the UI displays correctly relative to the Knox API semantics.

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
```

**Key insight**: State mapping is applied in BOTH directions (get and set) to ensure consistency.

---

## Policy Naming Conventions

### Prefer Negative Naming

Policies are typically named in the **negative** (Block, Disable, Restrict) because:
1. The default state is usually "allowed/enabled"
2. Policies **restrict** or **control** behavior
3. Security-focused naming (what is being blocked/disabled)

### Positive Naming Exceptions

Use positive naming when the feature is **opt-in** (disabled by default) or the policy **enables** a special capability. Examples: `EnableExtraBrightnessPolicy`, `EnableHdmPolicy`, `AlwaysRadioOnEnabledPolicy`.

---

## Creating a BooleanStatePolicy

First create getter and setter use cases using `/knox-usecase-creation`, then create the policy:

```kotlin
@PolicyDefinition(
    title = "[Title in Title Case]",
    description = "[Human-readable description of what the policy does]",
    category = PolicyCategory.Toggle,
    capabilities = [
        PolicyCapability.MODIFIES_[SUBSYSTEM],
        PolicyCapability.REQUIRES_[HARDWARE],
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

### Concrete Example

```kotlin
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

---

## Creating a ConfigurableStatePolicy

### Step 1: Create the PolicyState

```kotlin
data class NrModeState(
    override val isEnabled: Boolean,
    override val isSupported: Boolean = true,
    override val error: ApiError? = null,
    override val exception: Throwable? = null,
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
data class NrModeConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NrModeState, LteNrModeDto> {

    override fun fromApiData(apiData: LteNrModeDto): NrModeState {
        return NrModeState(
            isEnabled = (apiData.mode != LteNrMode.EnableBothSaAndNsa),
            mode = apiData.mode,
            simSlotId = apiData.simSlotId
        )
    }

    override fun toApiData(state: NrModeState): LteNrModeDto {
        val dto = LteNrModeDto(state.simSlotId)
        return when (state.isEnabled) {
            true -> dto.copy(mode = state.mode)
            false -> dto.copy(mode = LteNrMode.EnableBothSaAndNsa)
        }
    }

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

### Step 3: Create Use Cases

Create getter and setter use cases using `/knox-usecase-creation`.

### Step 4: Create the Policy

```kotlin
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

data class NrModeParameters(val simSlotId: Int? = null) : PolicyParameters
```

---

## Configuration Option Types

### Toggle
```kotlin
ConfigurationOption.Toggle(key = "useRedOverlay", label = "Use red overlay?", isEnabled = state.useRedOverlay)
```

### Choice
```kotlin
ConfigurationOption.Choice(key = "mode", label = "Mode", options = listOf("Option A", "Option B"), selected = state.selectedOption)
```

### NumberInput
```kotlin
ConfigurationOption.NumberInput(key = "simSlotId", label = "SIM Slot Id", value = state.simSlotId ?: 0, range = 0..2)
```

### TextInput
```kotlin
ConfigurationOption.TextInput(key = "ssid", label = "Network SSID", value = state.ssid, hint = "Enter network name", maxLength = 32)
```

### TextList
```kotlin
ConfigurationOption.TextList(key = "allowedDomains", label = "Allowed Domains", values = state.domains, hint = "Add domain")
```

---

## Policy Capabilities Reference

### What it Modifies
`MODIFIES_RADIO`, `MODIFIES_WIFI`, `MODIFIES_BLUETOOTH`, `MODIFIES_DISPLAY`, `MODIFIES_AUDIO`, `MODIFIES_CHARGING`, `MODIFIES_CALLING`, `MODIFIES_HARDWARE`, `MODIFIES_SECURITY`, `MODIFIES_NETWORK`, `MODIFIES_BROWSER`

### Device Requirements
`REQUIRES_SIM`, `REQUIRES_HDM`, `REQUIRES_DUAL_SIM`

### Impact Characteristics
`SECURITY_SENSITIVE`, `AFFECTS_CONNECTIVITY`, `AFFECTS_BATTERY`, `REQUIRES_REBOOT`, `PERSISTENT_ACROSS_REBOOT`

### Compliance
`STIG`

---

## Common Pitfalls

### 1. Wrong State Mapping
**Problem:** UI toggle state is opposite of expected.
**Solution:** Review whether policy name semantics match or oppose API semantics. Use INVERTED when they oppose.

### 2. Missing mapEnabled in Configuration
**Problem:** State mapping not applied in ConfigurableStatePolicy.
**Solution:** Call `mapEnabled()` in `fromApiData()` and `toApiData()` when needed.

### 3. fromUiState Applying State Mapping
**Problem:** State mapping applied twice (once in UI, once in fromUiState).
**Solution:** `fromUiState` should NOT apply state mapping - the UI state is already in the correct domain form.

### 4. Semantic isEnabled Derivation
**Problem:** `isEnabled` doesn't reflect meaningful state for ConfigurableStatePolicy.
**Solution:** Derive `isEnabled` from the actual semantic meaning:

```kotlin
// Good: isEnabled derived from mode
isEnabled = (apiData.mode != AutoCallPickupMode.Disable)

// Bad: Always true/false regardless of actual state
isEnabled = true
```

---

## File Organization

```
knox-[module]/
└── src/main/java/net/sfelabs/knox_[module]/domain/
    ├── policy/
    │   ├── [feature_group]/
    │   │   ├── [Feature]Policy.kt           # The policy class
    │   │   ├── [Feature]State.kt            # PolicyState (ConfigurableStatePolicy only)
    │   │   └── [Feature]Configuration.kt    # PolicyConfiguration (ConfigurableStatePolicy only)
    │   └── [SimpleFeature]Policy.kt         # BooleanStatePolicy (can be single file)
    └── use_cases/                           # See /knox-usecase-creation
```

## Checklist

- [ ] Determine policy type (BooleanStatePolicy vs ConfigurableStatePolicy)
- [ ] Determine state mapping (DIRECT vs INVERTED) based on naming semantics
- [ ] Create use cases (see `/knox-usecase-creation`)
- [ ] For ConfigurableStatePolicy: Create PolicyState data class
- [ ] For ConfigurableStatePolicy: Create PolicyConfiguration class
- [ ] Create policy class with @PolicyDefinition annotation
- [ ] Set appropriate capabilities
- [ ] Set correct PolicyCategory (Toggle vs ConfigurableToggle)
- [ ] Test state mapping works correctly in both directions
- [ ] Verify UI displays expected state
