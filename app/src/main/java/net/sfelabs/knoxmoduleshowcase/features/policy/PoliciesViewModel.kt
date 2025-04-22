package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.*
import net.sfelabs.core.knox.feature.domain.model.Policy
import net.sfelabs.core.knox.feature.domain.registry.PolicyRegistry
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState.ConfigurableToggle
import net.sfelabs.knox_tactical.domain.policy.AutoTouchSensitivityPolicy
import net.sfelabs.knox_tactical.domain.policy.Disable2GConnectivityPolicy
import net.sfelabs.knox_tactical.domain.policy.DisableElectronicSimPolicy
import net.sfelabs.knox_tactical.domain.policy.DisableHotspot20Policy
import net.sfelabs.knox_tactical.domain.policy.DisableRamPlusPolicy
import net.sfelabs.knox_tactical.domain.policy.EnableExtraBrightnessPolicy
import net.sfelabs.knox_tactical.domain.policy.LcdBacklightPolicy
import net.sfelabs.knox_tactical.domain.policy.TacticalDeviceModePolicy
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupPolicy
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.auto_record_policy.AutoRecordCallPolicy
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
import net.sfelabs.knox_tactical.domain.policy.pogo.DisablePogoKeyboardConnectionPolicy
import net.sfelabs.knox_tactical.generated.policy.PolicyType
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent
import javax.inject.Inject

@HiltViewModel
class PoliciesViewModel @Inject constructor(
    private val featureRegistry: PolicyRegistry
) : ViewModel() {
    private val _policies = MutableStateFlow<List<PolicyUiState>>(emptyList())
    val policies = _policies.asStateFlow()

    init {
        loadPolicies()
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            _policies.value = featureRegistry.getAllPolicies()
                .mapNotNull { createPolicyUiState(it) }
        }
    }

    fun onEvent(event: PolicyEvent) {
        when (event) {
            is PolicyEvent.UpdateConfiguration -> {
                viewModelScope.launch {
                    val policy = featureRegistry.getPolicyState(event.featureName) ?: return@launch

                    // Immediately update UI with new state and loading indicator
                    updateUiState(
                        event.featureName,
                        event.newUiState.copyWithLoading(isLoading = true)
                    )

                    val newState = updatePolicyState(policy, event.newUiState)

                    try {
                        when (val result = featureRegistry.setPolicyState(policy.key, newState)) {
                            is ApiResult.Success -> {
                                createPolicyUiState(
                                    Policy(policy.key, PolicyStateWrapper(newState))
                                )?.let { updatedUiState ->
                                    updateUiState(
                                        event.featureName,
                                        updatedUiState
                                    )
                                }
                            }
                            is ApiResult.Error -> {
                                val errorState = event.newUiState.copyWithError(
                                    error = result.apiError.message
                                )
                                updateUiState(event.featureName, errorState)
                            }
                            ApiResult.NotSupported -> {
                                updateUiState(
                                    event.featureName,
                                    event.newUiState.copyWithLoading(isLoading = false)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        val errorState = event.newUiState.copyWithError(
                            error = e.message ?: "Unknown error"
                        )
                        updateUiState(event.featureName, errorState)
                    }
                }
            }
        }
    }

    private fun updatePolicyState(policy: Policy<*>, uiState: PolicyUiState): PolicyState {
        return when (PolicyType.fromPolicy(policy)) {
            PolicyType.EnableHdmPolicy -> EnableHdmPolicy()
            PolicyType.AutoCallPickupPolicy -> AutoCallPickupPolicy()
            PolicyType.BandLocking5gPolicy -> BandLocking5gPolicy()
            PolicyType.AutoRecordCallPolicy -> AutoRecordCallPolicy()
            PolicyType.AutoTouchSensitivityPolicy -> AutoTouchSensitivityPolicy()
            PolicyType.Disable2GConnectivityPolicy -> Disable2GConnectivityPolicy()
            PolicyType.DisableElectronicSimPolicy -> DisableElectronicSimPolicy()
            PolicyType.DisableHotspot20Policy -> DisableHotspot20Policy()
            PolicyType.DisableImsPolicy -> DisableImsPolicy()
            PolicyType.DisableRamPlusPolicy -> DisableRamPlusPolicy()
            PolicyType.EnableExtraBrightnessPolicy -> EnableExtraBrightnessPolicy()
            PolicyType.EnableNightVisionModePolicy -> EnableNightVisionModePolicy()
            PolicyType.LcdBacklightPolicy -> LcdBacklightPolicy()
            PolicyType.LteBandLockingPolicy -> LteBandLockingPolicy()
            PolicyType.NrModePolicy -> NrModePolicy()
            PolicyType.TacticalDeviceModePolicy -> TacticalDeviceModePolicy()
            PolicyType.DisablePogoKeyboardConnectionPolicy -> DisablePogoKeyboardConnectionPolicy()
        }.fromUiState(uiState.isEnabled, uiState.currentOptions())
    }

    private fun createPolicyUiState(policy: Policy<*>): PolicyUiState? {
        val component = featureRegistry.getComponent(policy.key) ?: return null
        val state = policy.state.value

        return when (state) {
            is BooleanPolicyState -> PolicyUiState.Toggle(
                title = component.title,
                policyName = component.policyName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message
            )
            else -> ConfigurableToggle(
                title = component.title,
                policyName = component.policyName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message,
                configurationOptions = createConfigurationOptions(policy)
            )
        }
    }

    private fun createConfigurationOptions(feature: Policy<PolicyState>): List<ConfigurationOption> {
        val type = PolicyType.fromPolicy(feature)
        return when (type) {
            PolicyType.AutoCallPickupPolicy -> {
                val state = feature.state.value as AutoCallPickupState
                val policy = AutoCallPickupPolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.BandLocking5gPolicy -> {
                val state = feature.state.value as BandLockingState
                val policy = BandLocking5gPolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.DisableImsPolicy -> {
                val state = feature.state.value as ImsState
                val policy = DisableImsPolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.EnableHdmPolicy -> {
                val state = feature.state.value as HdmState
                val policy = EnableHdmPolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.EnableNightVisionModePolicy -> {
                val state = feature.state.value as NightVisionState
                val policy = EnableNightVisionModePolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.LteBandLockingPolicy -> {
                val state = feature.state.value as BandLockingState
                val policy = LteBandLockingPolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.NrModePolicy -> {
                val state = feature.state.value as NrModeState
                val policy = NrModePolicy()
                policy.getConfigurationOptions(state)
            }
            PolicyType.AutoRecordCallPolicy,
            PolicyType.AutoTouchSensitivityPolicy,
            PolicyType.Disable2GConnectivityPolicy,
            PolicyType.DisableElectronicSimPolicy,
            PolicyType.DisableHotspot20Policy,
            PolicyType.DisableRamPlusPolicy,
            PolicyType.EnableExtraBrightnessPolicy,
            PolicyType.LcdBacklightPolicy,
            PolicyType.TacticalDeviceModePolicy,
            PolicyType.DisablePogoKeyboardConnectionPolicy -> emptyList()
        }
    }

    private fun updateUiState(featureName: String, uiState: PolicyUiState) {
        _policies.value = _policies.value.map {
            if (it.policyName == featureName) uiState else it
        }
    }
}