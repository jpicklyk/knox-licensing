package net.sfelabs.knox_tactical.domain.policy.night_vision

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class NightVisionConfiguration(
    val enabled: Boolean,
    val useRedOverlay: Boolean,
    override val stateMapping: StateMapping
) : PolicyConfiguration<NightVisionState> {
    override fun toState(currentState: NightVisionState): NightVisionState {
        return currentState.copy(
            isEnabled = mapEnabled(enabled),
            useRedOverlay = useRedOverlay
        )
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = listOf(
        ConfigurationOption.Toggle(
            key = "enabled",
            label = "Enabled",
            isEnabled = enabled
        ),
    )

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): NightVisionState {
        val useRedOverlay = options.filterIsInstance<ConfigurationOption.Toggle>()
            .find { it.key == "enabled" }
            ?.isEnabled == true
        return NightVisionState(
            isEnabled = mapEnabled(enabled),
            useRedOverlay = useRedOverlay
        )
    }
}