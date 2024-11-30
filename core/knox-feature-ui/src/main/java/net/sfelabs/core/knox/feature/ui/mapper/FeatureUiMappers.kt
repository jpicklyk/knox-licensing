package net.sfelabs.core.knox.feature.ui.mapper

import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistration
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState

fun Feature<Boolean>.toUiState(registration: FeatureRegistration<Boolean>) = FeatureUiState.Toggle(
    name = key.featureName,
    description = registration.description,
    isEnabled = state.enabled,
    isLoading = false,
    error = null
)