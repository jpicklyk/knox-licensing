package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.api.*
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.model.LteNrMode
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmComponent
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmComponentConfig
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmState
import net.sfelabs.knox_tactical.domain.policy.night_vision.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.nr_mode.NrModeState
import net.sfelabs.knox_tactical.generated.feature.PolicyType
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent
import javax.inject.Inject

@HiltViewModel
class PoliciesViewModel @Inject constructor(
    private val featureRegistry: FeatureRegistry
) : ViewModel() {
    private val _policies = MutableStateFlow<List<PolicyUiState>>(emptyList())
    val policies = _policies.asStateFlow()

    init {
        loadPolicies()
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            _policies.value = featureRegistry.getAllFeatures()
                .mapNotNull { createPolicyUiState(it) }
        }
    }

    fun onEvent(event: PolicyEvent) {
        when (event) {
            is PolicyEvent.UpdateEnabled -> {
                viewModelScope.launch {
                    val feature = featureRegistry.getFeature(event.featureName) ?: return@launch
                    val currentState = feature.state.value
                    val newState = currentState.withEnabled(enabled = event.isEnabled)
                    updatePolicy(newState, event.featureName)
                }
            }
            is PolicyEvent.UpdateConfiguration -> {
                viewModelScope.launch {
                    val feature = featureRegistry.getFeature(event.featureName) ?: return@launch
                    val currentState = feature.state.value
                    val newState = updatePolicyState(currentState, event.key, event.value)
                    updatePolicy(newState, event.featureName)
                }
            }

            is PolicyEvent.SaveConfiguration -> TODO()
        }
    }

    private fun updatePolicyState(currentState: PolicyState, key: String, value: Any): PolicyState =
        when (currentState) {
            is BooleanPolicyState -> currentState
            is HdmState -> updateHdmState(currentState, key, value)
            is AutoCallPickupState -> updateAutoCallPickupState(currentState, key, value)
            is BandLockingState -> updateBandLockingState(currentState, key, value)
            is NrModeState -> updateNrModeState(currentState, key, value)
            is NightVisionState -> updateNightVisionState(currentState, key, value)

            else -> currentState
        }

    private fun updateHdmState(state: HdmState, key: String, value: Any): HdmState {
        val component = HdmComponent.entries.firstOrNull {
            it.name.equals(key, ignoreCase = true)
        } ?: return state

        val newMask = if (value as? Boolean == true) {
            state.policyMask or component.mask
        } else {
            state.policyMask and component.mask.inv()
        }

        return state.copy(policyMask = newMask)
    }

    private fun updateAutoCallPickupState(state: AutoCallPickupState, key: String, value: Any): AutoCallPickupState {
        return when (key) {
            "mode" -> state.copy(mode = value as AutoCallPickupMode)
            else -> state
        }
    }

    private fun updateBandLockingState(state: BandLockingState, key: String, value: Any): BandLockingState {
        return when (key) {
            "band" -> state.copy(band = value as Int)
            "simSlotId" -> state.copy(simSlotId = value as Int)
            else -> state
        }
    }

    private fun updateNrModeState(state: NrModeState, key: String, value: Any): NrModeState {
        return when (key) {
            "mode" -> state.copy(mode = value as LteNrMode)
            "simSlotId" -> state.copy(simSlotId = value as Int)
            else -> state
        }
    }

    private fun updateNightVisionState(state: NightVisionState, key: String, value: Any): NightVisionState {
        return when (key) {
            "useRedOverlay" -> state.copy(useRedOverlay = value as Boolean)
            else -> state
        }
    }

    private fun updatePolicy(newState: PolicyState, featureName: String) {
        viewModelScope.launch {
            val feature = featureRegistry.getFeature(featureName) ?: return@launch
            try {
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch
                when (val result = handler.setState(newState)) {
                    is ApiResult.Success -> updateUiState(feature.key, newState)
                    is ApiResult.Error -> {
                        val errorState = newState.withError(result.apiError, result.exception)
                        updateUiState(feature.key, errorState)
                    }
                    ApiResult.NotSupported -> { /* No need to handle this case */ }
                }
            } catch (e: Exception) {
                val errorState = newState.withError(
                    DefaultApiError.UnexpectedError(e.message ?: "Unknown error"),
                    e
                )
                updateUiState(feature.key, errorState)
            }
        }
    }

    private suspend fun updateUiState(key: FeatureKey<*>, state: PolicyState) {
        featureRegistry.getFeature(key.featureName)?.let { feature ->
            createPolicyUiState(Feature(feature.key, PolicyStateWrapper(state)))?.let { uiState ->
                _policies.value = _policies.value.map {
                    if (it.featureName == key.featureName) uiState else it
                }
            }
        }
    }

    private fun createPolicyUiState(feature: Feature<*>): PolicyUiState? {
        val component = featureRegistry.getComponent(feature.key) ?: return null
        val state = feature.state.value

        return when (state) {
            is BooleanPolicyState -> PolicyUiState.Toggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message
            )
            else -> PolicyUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message,
                configurationOptions = createConfigurationOptions(state)
            )
        }
    }

    private fun createConfigurationOptions(state: PolicyState): List<ConfigurationOption> = when (state) {
        is BandLockingState -> buildList {
            add(ConfigurationOption.NumberInput(
                key = "band",
                label = "Band",
                value = state.band
            ))
            state.simSlotId?.let {
                add(ConfigurationOption.NumberInput(
                    key = "simSlotId",
                    label = "SIM Slot",
                    value = it,
                    range = 0..1
                ))
            }
        }
        is AutoCallPickupState -> listOf(
            ConfigurationOption.Choice(
                key = "mode",
                label = "Mode",
                selected = when (state.mode) {
                    AutoCallPickupMode.Disable -> "Disable"
                    AutoCallPickupMode.Enable -> "Enable"
                    AutoCallPickupMode.EnableAlwaysAccept -> "Enable Always Accept"
                },
                options = listOf("Enable", "Enable Always Accept")
            )
        )
        is NrModeState -> buildList {
            add(ConfigurationOption.Choice(
                key = "mode",
                label = "Mode",
                selected = when (state.mode) {
                    LteNrMode.EnableBothSaAndNsa -> "Enable Both SA and NSA"
                    LteNrMode.DisableSa -> "Disable SA"
                    LteNrMode.DisableNsa -> "Disable NSA"
                },
                options = listOf(
                    "Disable SA",
                    "Disable NSA"
                )
            ))
            state.simSlotId?.let {
                add(ConfigurationOption.NumberInput(
                    key = "simSlotId",
                    label = "SIM Slot",
                    value = it,
                    range = 0..1
                ))
            }
        }
        is NightVisionState -> listOf(
            ConfigurationOption.Toggle(
                key = "useRedOverlay",
                label = "Use Red Overlay",
                isEnabled = state.useRedOverlay
            )
        )
        is HdmState -> HdmComponent.entries.map { component ->
            ConfigurationOption.Toggle(
                key = component.name.lowercase(),
                label = component.displayName,
                isEnabled = (state.policyMask and component.mask) != 0
            )
        }
        else -> emptyList()
    }
}