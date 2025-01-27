package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.data.dto.ImsDto

data class ImsConfiguration(
    override val stateMapping: StateMapping
) : PolicyConfiguration<ImsState, ImsDto> {
    override fun fromApiData(apiData: ImsDto): ImsState {
        return ImsState(
            isEnabled = mapEnabled(apiData.enabled),
            simSlotId = apiData.simSlotId
        )
    }

    override fun toApiData(state: ImsState): ImsDto {
        return ImsDto(
            enabled = mapEnabled(state.isEnabled),
            simSlotId = state.simSlotId
        )

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