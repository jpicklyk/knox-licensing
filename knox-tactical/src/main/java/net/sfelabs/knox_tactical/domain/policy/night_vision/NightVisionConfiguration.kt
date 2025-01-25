package net.sfelabs.knox_tactical.domain.policy.night_vision

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class NightVisionConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NightVisionState, NightVisionState> {

    override fun fromApiData(apiData: NightVisionState): NightVisionState {
        return apiData.copy(
            isEnabled = mapEnabled(apiData.isEnabled)
        )
    }

    override fun toApiData(state: NightVisionState): NightVisionState {
        return state.copy(
            isEnabled = mapEnabled(state.isEnabled)
        )
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): NightVisionState {
        val useRedOverlay = options.filterIsInstance<ConfigurationOption.Toggle>()
            .find { it.key == "useRedOverlay" }
            ?.isEnabled == true
        return NightVisionState(
            isEnabled = uiEnabled,
            useRedOverlay = useRedOverlay
        )
    }

    override fun getConfigurationOptions(state: NightVisionState): List<ConfigurationOption> = listOf(
        ConfigurationOption.Toggle(
            key = "useRedOverlay",
            label = "Use red overlay?",
            isEnabled = state.isEnabled
        )
    )
}