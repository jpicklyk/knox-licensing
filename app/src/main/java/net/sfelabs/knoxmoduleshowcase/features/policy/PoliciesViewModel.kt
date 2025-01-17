package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.core.knox.feature.api.BooleanPolicyState
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
            // Simple boolean toggle state
            is BooleanPolicyState -> FeatureUiState.Toggle(
                title = component.title,
                featureName = component.featureName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                error = state.error?.message
            )

            // Complex states
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

    fun toggleFeature(featureName: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                when (val currentState = feature.state.value) {
                    is BooleanPolicyState -> handler.setState(currentState.copy(isEnabled = enabled))
                    is BandLockingState -> handler.setState(currentState.copy(isEnabled = enabled))
                    is AutoCallPickupState -> handler.setState(currentState.copy(isEnabled = enabled))
                    is NightVisionState -> handler.setState(currentState.copy(isEnabled = enabled))
                    is ImsState -> handler.setState(currentState.copy(isEnabled = enabled))
                }

                // Update just this feature in the UI
                featureRegistry.getFeature(featureName)?.let { updatedFeature ->
                    createFeatureUiState(updatedFeature)?.let { uiState ->
                        _features.value = _features.value.map {
                            if (it.featureName == featureName) uiState else it
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error toggling feature: ${e.message}")
            }
        }
    }

    fun updateFeatureConfig(featureName: String, key: String, value: Any) {
        viewModelScope.launch {
            try {
                val feature = featureRegistry.getFeature(featureName) ?: return@launch
                val handler = featureRegistry.getHandler(feature.key) ?: return@launch

                when (val currentState = feature.state.value) {
                    is BandLockingState -> {
                        val newState = when (key) {
                            "band" -> currentState.copy(band = value as Int)
                            "simSlotId" -> currentState.copy(simSlotId = value as Int)
                            else -> currentState
                        }
                        handler.setState(newState)
                    }
                    is AutoCallPickupState -> {
                        if (key == "mode") {
                            handler.setState(currentState.copy(mode = value as AutoCallPickupMode))
                        }
                    }
                    is NightVisionState -> {
                        if (key == "useRedOverlay") {
                            handler.setState(currentState.copy(useRedOverlay = value as Boolean))
                        }
                    }
                    is ImsState -> {
                        val newState = when (key) {
                            "feature" -> currentState.copy(feature = value as Int)
                            "simSlotId" -> currentState.copy(simSlotId = value as Int)
                            else -> currentState
                        }
                        handler.setState(newState)
                    }
                }

                // Update just this feature in the UI
                featureRegistry.getFeature(featureName)?.let { updatedFeature ->
                    createFeatureUiState(updatedFeature)?.let { uiState ->
                        _features.value = _features.value.map {
                            if (it.featureName == featureName) uiState else it
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error updating feature config: ${e.message}")
            }
        }
    }
}