package net.sfelabs.knox_tactical.domain.mapper


import net.sfelabs.core.knox.feature.internal.component.FeatureComponent
import net.sfelabs.core.knox.feature.internal.model.Feature
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState
import net.sfelabs.knox_tactical.domain.model.NightVisionState

fun NightVisionState.toConfigurationMap() = mapOf(
    "useRedOverlay" to useRedOverlay
)

fun Map<String, Any>.toNightVisionState(isEnabled: Boolean) = NightVisionState(
    isEnabled = isEnabled,
    useRedOverlay = this["useRedOverlay"] as? Boolean ?: false
)

fun Feature<NightVisionState>.toUiState(component: FeatureComponent<NightVisionState>) =
    FeatureUiState.ConfigurableToggle(
        title = component.title,
        featureName = key.featureName,
        description = component.description,
        isEnabled = state.enabled,
        configurationOptions = state.value.toConfigurationMap()
    )