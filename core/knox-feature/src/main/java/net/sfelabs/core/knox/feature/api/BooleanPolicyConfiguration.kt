package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class BooleanPolicyConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BooleanPolicyState, Boolean> {

    override fun fromApiData(apiData: Boolean): BooleanPolicyState {
        return BooleanPolicyState(
            isEnabled = mapEnabled(apiData)
        )
    }

    override fun toApiData(state: BooleanPolicyState): Boolean {
        return mapEnabled(state.isEnabled)
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): BooleanPolicyState {
        return BooleanPolicyState(
            isEnabled = uiEnabled
        )
    }

    override fun getConfigurationOptions(state: BooleanPolicyState): List<ConfigurationOption> = emptyList()
}
