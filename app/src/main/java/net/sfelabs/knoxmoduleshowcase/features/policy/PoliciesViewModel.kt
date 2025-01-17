package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.BooleanPolicyState
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.model.ImsState
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import javax.inject.Inject

@HiltViewModel
class PoliciesViewModel @Inject constructor(
    private val featureRegistry: FeatureRegistry
) : ViewModel() {
    private val _features = MutableStateFlow<List<FeatureUiState>>(emptyList())
    val features = _features.asStateFlow()

    init {
        loadFeatures()
    }

    private fun loadFeatures() {
        viewModelScope.launch {
            _features.value = featureRegistry.getAllFeatures()
                .mapNotNull { createFeatureUiState(it) }
        }
    }

    private fun createFeatureUiState(feature: Feature<*>): FeatureUiState? {
        val component = featureRegistry.getComponent(feature.key) ?: return null
        return when (val state = feature.state.value) {
            is BooleanPolicyState -> FeatureUiState.Toggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message
            )
            is BandLockingState -> FeatureUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message,
                configurationOptions = mapOf(
                    "band" to state.band,
                    "simSlotId" to state.simSlotId
                ).filterValues { it != null }
            )
            is AutoCallPickupState -> FeatureUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message,
                configurationOptions = mapOf(
                    "mode" to state.mode
                )
            )
            is NightVisionState -> FeatureUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message,
                configurationOptions = mapOf(
                    "useRedOverlay" to state.useRedOverlay
                )
            )
            is ImsState -> FeatureUiState.ConfigurableToggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message,
                configurationOptions = mapOf(
                    "feature" to state.feature,
                    "simSlotId" to state.simSlotId
                )
            )
            else -> null
        }
    }

    private fun updateFeatureUiState(
        feature: Feature<*>,
        state: PolicyState,
        featureName: String
    ) {
        createFeatureUiState(Feature(
            key = feature.key,
            state = PolicyStateWrapper(state)
        ))?.let { uiState ->
            _features.value = _features.value.map {
                if (it.featureName == featureName) uiState else it
            }
        }
    }

    private fun handleSetStateResult(
        result: ApiResult<Unit>,
        feature: Feature<*>,
        updatedState: PolicyState,
        featureName: String
    ) {
        when (result) {
            is ApiResult.Success -> updateFeatureUiState(feature, updatedState, featureName)
            is ApiResult.Error -> updateFeatureUiState(
                feature,
                updatedState.withError(result.apiError, result.exception),
                featureName
            )
            ApiResult.NotSupported -> { /* No need to handle this case */ }
        }
    }

    private suspend fun refreshFeatureState(featureName: String) {
        featureRegistry.getFeature(featureName)?.let { updatedFeature ->
            createFeatureUiState(updatedFeature)?.let { uiState ->
                _features.value = _features.value.map {
                    if (it.featureName == featureName) uiState else it
                }
            }
        }
    }

    fun toggleFeature(featureName: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                // Create updated state
                val currentState = feature.state.value
                val updatedState = when (currentState) {
                    is BooleanPolicyState -> currentState.copy(isEnabled = enabled)
                    is BandLockingState -> currentState.copy(isEnabled = enabled)
                    is AutoCallPickupState -> currentState.copy(isEnabled = enabled)
                    is NightVisionState -> currentState.copy(isEnabled = enabled)
                    is ImsState -> currentState.copy(isEnabled = enabled)
                    else -> return@launch
                }

                handleSetStateResult(
                    result = handler.setState(updatedState),
                    feature = feature,
                    updatedState = updatedState,
                    featureName = featureName
                )
            } catch (e: Exception) {
                println("Error toggling feature: ${e.message}")
                refreshFeatureState(featureName)
            }
        }
    }

    fun updateFeatureConfig(featureName: String, key: String, value: Any) {
        viewModelScope.launch {
            try {
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                // Create updated state
                val currentState = feature.state.value
                val updatedState = when (currentState) {
                    is BandLockingState -> when (key) {
                        "band" -> currentState.copy(band = value as Int)
                        "simSlotId" -> currentState.copy(simSlotId = value as Int)
                        else -> currentState
                    }
                    is AutoCallPickupState -> when (key) {
                        "mode" -> currentState.copy(mode = value as AutoCallPickupMode)
                        else -> currentState
                    }
                    is NightVisionState -> when (key) {
                        "useRedOverlay" -> currentState.copy(useRedOverlay = value as Boolean)
                        else -> currentState
                    }
                    is ImsState -> when (key) {
                        "feature" -> currentState.copy(feature = value as Int)
                        "simSlotId" -> currentState.copy(simSlotId = value as Int)
                        else -> currentState
                    }
                    else -> return@launch
                }

                handleSetStateResult(
                    result = handler.setState(updatedState),
                    feature = feature,
                    updatedState = updatedState,
                    featureName = featureName
                )
            } catch (e: Exception) {
                println("Error updating feature config: ${e.message}")
                refreshFeatureState(featureName)
            }
        }
    }
}