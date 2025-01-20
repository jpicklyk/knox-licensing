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
import net.sfelabs.knox_tactical.domain.model.ImsState
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.policy.nr_mode.NrModeState
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
            is PolicyEvent.UpdatePolicy -> updatePolicy(event.policyState, event.featureName)
        }
    }

    private fun updatePolicy(newState: PolicyState, featureName: String) {
        viewModelScope.launch {
            val feature = featureRegistry.getFeature(featureName) ?: return@launch
            try {
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                when (val result = handler.setState(newState)) {
                    is ApiResult.Success -> {
                        updateUiState(feature.key, newState)
                    }
                    is ApiResult.Error -> {
                        val errorState = newState.withError(result.apiError, result.exception)
                        updateUiState(feature.key, errorState)
                    }
                    ApiResult.NotSupported -> { /* No need to handle this case */ }
                }
            } catch (e: Exception) {
                println("Error updating policy: ${e.message}")
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
                error = state.error?.message
            )
            else -> PolicyUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message,
                configurationOptions = getConfigurationOptions(state)
            )
        }
    }

    private fun getConfigurationOptions(state: PolicyState): Map<String, Any?> = when (state) {
        is BandLockingState -> mapOf(
            "band" to state.band,
            "simSlotId" to state.simSlotId
        )
        is AutoCallPickupState -> mapOf(
            "mode" to state.mode
        )
        is NrModeState -> mapOf(
            "mode" to state.mode,
            "simSlotId" to state.simSlotId
        )
        is NightVisionState -> mapOf(
            "useRedOverlay" to state.useRedOverlay
        )
        is ImsState -> mapOf(
            "simSlotId" to state.simSlotId
        )
        else -> emptyMap()
    }//.filterValues { it != null }
}