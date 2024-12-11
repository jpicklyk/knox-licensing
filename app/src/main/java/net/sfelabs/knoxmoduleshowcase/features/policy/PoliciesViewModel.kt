package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.core.knox.feature.internal.model.Feature
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import net.sfelabs.core.knox.feature.internal.registry.FeatureRegistry
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState
import javax.inject.Inject

@HiltViewModel
class PoliciesViewModel @Inject constructor(
    private val featureRegistry: FeatureRegistry
) : ViewModel() {
    private val _features = MutableStateFlow<List<FeatureUiState.Toggle>>(emptyList())
    val features = _features.asStateFlow()

    init {
        loadFeatures()
    }

    private fun loadFeatures() {
        viewModelScope.launch {
            val allFeatures = featureRegistry.getFeatures(FeatureCategory.Toggle)
            println("Found features: ${allFeatures.size}")

            val toggleFeatures = featureRegistry.getFeatures(FeatureCategory.Toggle)
                .filterIsInstance<Feature<Boolean>>()
                .mapNotNull { feature ->
                    featureRegistry.getComponent(feature.key)?.let { component ->
                        FeatureUiState.Toggle(
                            name = component.title,
                            description = component.description,
                            isEnabled = feature.state.enabled
                        )
                    }
                }
            println("Final toggle features: ${toggleFeatures.size}") // Debug log
            _features.value = toggleFeatures
        }
    }

    fun toggleFeature(name: String, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val features = featureRegistry.getFeatures(FeatureCategory.Toggle)
                    .filterIsInstance<Feature<Boolean>>()
                val feature = features.firstOrNull { it.key.featureName == name }

                feature?.let {
                    featureRegistry.getHandler(it.key)?.setState(
                        FeatureState(enabled = enabled, value = enabled)
                    )
                }
                loadFeatures() // Refresh state
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}