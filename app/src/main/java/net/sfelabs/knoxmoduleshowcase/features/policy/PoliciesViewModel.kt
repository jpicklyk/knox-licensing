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
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState.ConfigurableToggle
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupPolicy
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLocking5gPolicy
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.policy.band_locking.LteBandLockingPolicy
import net.sfelabs.knox_tactical.domain.policy.hdm.EnableHdmPolicy
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmState
import net.sfelabs.knox_tactical.domain.policy.modem_ims.DisableImsPolicy
import net.sfelabs.knox_tactical.domain.policy.modem_ims.ImsState
import net.sfelabs.knox_tactical.domain.policy.night_vision.EnableNightVisionModePolicy
import net.sfelabs.knox_tactical.domain.policy.night_vision.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.nr_mode.NrModePolicy
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
            is PolicyEvent.UpdateConfiguration -> {
                viewModelScope.launch {
                    val feature = featureRegistry.getPolicyState(event.featureName) ?: return@launch
                    val newState = updatePolicyState(feature, event.newUiState)
                    try {
                        when (val result = featureRegistry.setPolicyState(feature.key, newState)) {
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
        }
    }

    private fun updatePolicyState(feature: Feature<*>, uiState: PolicyUiState): PolicyState {
        return when (PolicyType.fromFeature(feature)) {
            PolicyType.EnableHdmPolicy -> {
                val policy = EnableHdmPolicy()
                val state = feature.state.value as HdmState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.AutoCallPickupPolicy -> {
                val policy = AutoCallPickupPolicy()
                val state = feature.state.value as AutoCallPickupState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.BandLocking5gPolicy -> {
                val policy = BandLocking5gPolicy()
                val state = feature.state.value as BandLockingState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.DisableImsPolicy -> {
                val policy = DisableImsPolicy()
                val state = feature.state.value as ImsState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.EnableNightVisionModePolicy -> {
                val policy = EnableNightVisionModePolicy()
                val state = feature.state.value as NightVisionState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.LteBandLockingPolicy -> {
                val policy = LteBandLockingPolicy()
                val state = feature.state.value as BandLockingState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            PolicyType.NrModePolicy -> {
                val policy = NrModePolicy()
                val state = feature.state.value as NrModeState
                policy.toConfiguration(state).fromConfigurationOptions(
                    uiState.currentOptions(),
                    uiState.isEnabled
                )
            }
            else -> {
                feature.state.value.withEnabled(enabled = uiState.isEnabled)
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
            else -> ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message,
                configurationOptions = createConfigurationOptions(feature)
            )
        }
    }

    /**
     * Create a list of configuration options for the given policy.
     */
    private fun createConfigurationOptions(feature: Feature<PolicyState>): List<ConfigurationOption>  {
        val type = PolicyType.fromFeature(feature)
        return when (type) {
            PolicyType.AutoCallPickupPolicy -> {
                val state = feature.state.value as AutoCallPickupState
                val policy = AutoCallPickupPolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.AutoRecordCallPolicy -> return emptyList()
            PolicyType.AutoTouchSensitivityPolicy -> return emptyList()
            PolicyType.BandLocking5gPolicy -> {
                val state = feature.state.value as BandLockingState
                val policy = BandLocking5gPolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.Disable2GConnectivityPolicy -> emptyList()
            PolicyType.DisableElectronicSimPolicy -> emptyList()
            PolicyType.DisableHotspot20Policy -> emptyList()
            PolicyType.DisableImsPolicy -> {
                val state = feature.state.value as ImsState
                val policy = DisableImsPolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.DisableRamPlusPolicy -> emptyList()
            PolicyType.EnableExtraBrightnessPolicy -> emptyList()
            PolicyType.EnableHdmPolicy -> {
                val state = feature.state.value as HdmState
                val policy = EnableHdmPolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.EnableNightVisionModePolicy -> {
                val state = feature.state.value as NightVisionState
                val policy = EnableNightVisionModePolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.LcdBacklightPolicy -> emptyList()
            PolicyType.LteBandLockingPolicy -> {
                val state = feature.state.value as BandLockingState
                val policy = LteBandLockingPolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.NrModePolicy -> {
                val state = feature.state.value as NrModeState
                val policy = NrModePolicy()
                policy.toConfiguration(state).toConfigurationOptions()
            }
            PolicyType.TacticalDeviceModePolicy -> emptyList()
        }
    }

    private suspend fun updateUiState(key: FeatureKey<*>, state: PolicyState) {
        featureRegistry.getPolicyState(key.featureName)?.let { feature ->
            createPolicyUiState(Feature(feature.key, PolicyStateWrapper(state)))?.let { uiState ->
                _policies.value = _policies.value.map {
                    if (it.featureName == key.featureName) uiState else it
                }
            }
        }
    }
}