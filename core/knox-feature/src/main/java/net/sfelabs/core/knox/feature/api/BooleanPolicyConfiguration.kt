package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class BooleanPolicyConfiguration(
    val enabled: Boolean,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BooleanPolicyState> {
    override fun toState(currentState: BooleanPolicyState): BooleanPolicyState {
        val mappedEnabled = when (stateMapping) {
            StateMapping.DIRECT -> enabled
            StateMapping.INVERTED -> !enabled
        }
        return currentState.copy(isEnabled = mappedEnabled)
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = emptyList()
    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
    ): BooleanPolicyState {
        return BooleanPolicyState(
            isEnabled = mapEnabled(enabled)
        )
    }
}
