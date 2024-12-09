package net.sfelabs.core.knox.feature.ui.mapper

import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.model.FeatureComponent
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistration
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState

fun Feature<Boolean>.toUiState(component: FeatureComponent<Boolean>) = FeatureUiState.Toggle(
    name = key.featureName,
    description = component.description,
    isEnabled = state.enabled,
    isLoading = false,
    error = null
)