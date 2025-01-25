package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class ImsConfiguration(
    override val stateMapping: StateMapping
) : PolicyConfiguration<ImsState, Boolean> {
    override fun fromApiData(apiEnabled: Boolean): ImsState {
        return ImsState(
            isEnabled = mapEnabled(apiEnabled),
            simSlotId = 0  // Default value
        )
    }

    override fun toApiData(state: ImsState): Boolean {
        return mapEnabled(state.isEnabled)
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): ImsState {
        val simSlotId = options
            .filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }
            ?.value
            ?: 0

        return ImsState(
            isEnabled = uiEnabled,  // UI state is already in correct domain form
            simSlotId = simSlotId
        )
    }

    override fun getConfigurationOptions(state: ImsState): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = state.simSlotId,
            range = 0..2
        )
    )
}