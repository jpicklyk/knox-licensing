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
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.model.ImsState
import net.sfelabs.knox_tactical.domain.model.LteNrMode
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.policy.nr_mode.NrModeState
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

    private fun createPolicyUiState(feature: Feature<*>): PolicyUiState? {
        val component = featureRegistry.getComponent(feature.key) ?: return null
        val state = feature.state.value

        val commonProperties = PolicyStateProperties(
            title = component.title,
            featureName = component.featureName,
            description = component.description,
            isEnabled = state.isEnabled,
            isSupported = state.isSupported,
            error = state.error?.message
        )

        return when (state) {
            is BooleanPolicyState -> createToggleState(commonProperties)
            else -> createConfigurableState(commonProperties, getConfigurationOptions(state))
        }
    }

    private data class PolicyStateProperties(
        val title: String,
        val featureName: String,
        val description: String,
        val isEnabled: Boolean,
        val isSupported: Boolean,
        val error: String?
    )

    private fun createToggleState(props: PolicyStateProperties) = PolicyUiState.Toggle(
        title = props.title,
        featureName = props.featureName,
        description = props.description,
        isEnabled = props.isEnabled,
        isSupported = props.isSupported,
        error = props.error
    )

    private fun createConfigurableState(
        props: PolicyStateProperties,
        configurationOptions: Map<String, Any?>
    ) = PolicyUiState.ConfigurableToggle(
        title = props.title,
        featureName = props.featureName,
        description = props.description,
        isEnabled = props.isEnabled,
        isSupported = props.isSupported,
        error = props.error,
        configurationOptions = configurationOptions
    )

    private fun getConfigurationOptions(state: PolicyState): Map<String, Any?> = when (state) {
        is BandLockingState -> mapOf(
            "band" to state.band,
            "simSlotId" to state.simSlotId
        )
        is AutoCallPickupState -> mapOf(
            "mode" to state.mode
        )
        is NightVisionState -> mapOf(
            "useRedOverlay" to state.useRedOverlay
        )
        is ImsState -> mapOf(
            "simSlotId" to state.simSlotId
        )
        is NrModeState -> mapOf(
            "mode" to state.mode
        )
        else -> emptyMap()
    }.filterValues { it != null }

    fun toggleFeature(featureName: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                val currentState = feature.state.value
                val updatedState = when (currentState) {
                    is BooleanPolicyState -> currentState.copy(isEnabled = enabled)
                    is BandLockingState -> currentState.copy(isEnabled = enabled)
                    is AutoCallPickupState -> currentState.copy(isEnabled = enabled)
                    is NightVisionState -> currentState.copy(isEnabled = enabled)
                    is ImsState -> currentState.copy(isEnabled = enabled)
                    is NrModeState -> currentState.copy(isEnabled = enabled)
                    else -> return@launch
                }

                when (val result = handler.setState(updatedState)) {
                    is ApiResult.Success -> {
                        updatePolicyState(feature.key, updatedState)
                    }
                    is ApiResult.Error -> {
                        val errorState = updatedState.withError(result.apiError, result.exception)
                        updatePolicyState(feature.key, errorState)
                    }
                    ApiResult.NotSupported -> { /* No need to handle this case */ }
                }
            } catch (e: Exception) {
                println("Error toggling feature: ${e.message}")
                // Update with current state plus error instead of reloading all policies
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val currentState = feature.state.value
                val errorState = when (currentState) {
                    is BooleanPolicyState -> currentState.copy(error = DefaultApiError.UnexpectedError(e.message ?: "Unknown error"), exception = e)
                    else -> currentState.withError(DefaultApiError.UnexpectedError(e.message ?: "Unknown error"), e)
                }
                updatePolicyState(feature.key, errorState)
            }
        }
    }

    fun updateFeatureConfig(featureName: String, key: String, value: Any) {
        viewModelScope.launch {
            val feature = featureRegistry.getFeature(featureName) ?: return@launch

            val currentState = feature.state.value
            val updatedState = when (currentState) {
                is BandLockingState -> when (key) {
                    "band" -> currentState.copy(band = value as Int)
                    "simSlotId" -> currentState.copy(simSlotId = value as Int)
                    else -> currentState
                }
                is AutoCallPickupState -> when (key) {
                    "mode" -> currentState.copy(
                        isEnabled = true,
                        mode = value as AutoCallPickupMode
                    )
                    else -> currentState
                }
                is NightVisionState -> when (key) {
                    "useRedOverlay" -> currentState.copy(
                        isEnabled = true,
                        useRedOverlay = value as Boolean
                    )
                    else -> currentState
                }
                is ImsState -> when (key) {
                    "simSlotId" -> currentState.copy(simSlotId = value as Int)
                    else -> currentState
                }
                is NrModeState -> when (key) {
                    "mode" -> currentState.copy(
                        isEnabled = true,
                        mode = value as LteNrMode
                    )
                    else -> currentState
                }
                else -> return@launch
            }
            updatePolicyState(feature.key, updatedState)
        }
    }

    private suspend fun updatePolicyState(key: FeatureKey<*>, state: PolicyState) {
        featureRegistry.getFeature(key.featureName)?.let { feature ->
            createPolicyUiState(Feature(feature.key, PolicyStateWrapper(state)))?.let { uiState ->
                _policies.value = _policies.value.map {
                    if (it.featureName == key.featureName) uiState else it
                }
            }
        }
    }
}