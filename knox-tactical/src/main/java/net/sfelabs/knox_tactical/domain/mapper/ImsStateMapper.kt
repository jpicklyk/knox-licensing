package net.sfelabs.knox_tactical.domain.mapper

import net.sfelabs.core.knox.feature.internal.component.FeatureComponent
import net.sfelabs.core.knox.feature.internal.model.Feature
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState
import net.sfelabs.knox_tactical.domain.model.ImsState

fun ImsState.toConfigurationMap() = mapOf(
    "simSlotId" to simSlotId,
    "feature" to feature
)

fun Map<String, Any>.toImsState(isEnabled: Boolean) = ImsState(
    isEnabled = isEnabled,
    simSlotId = this["simSlotId"] as? Int ?: 0,
    feature = this["feature"] as? Int ?: 1
)

fun Feature<ImsState>.toUiState(component: FeatureComponent<ImsState>) =
    FeatureUiState.ConfigurableToggle(
        title = component.title,
        featureName = key.featureName,
        description = component.description,
        isEnabled = state.enabled,
        configurationOptions = state.value.toConfigurationMap()
    )