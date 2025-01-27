package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption
import net.sfelabs.knox_tactical.data.dto.BandLockingDto

data class BandLockingConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BandLockingState, BandLockingDto> {

    override fun fromApiData(apiData: BandLockingDto): BandLockingState {
        return BandLockingState(
            isEnabled = mapEnabled(apiData.band != NO_BAND_LOCK),
            band = apiData.band,
            simSlotId = apiData.simSlotId
        )
    }

    override fun toApiData(state: BandLockingState): BandLockingDto {
        val band = if(mapEnabled(state.isEnabled))
            state.band
        else
            NO_BAND_LOCK
        return BandLockingDto(
            band = band,
            simSlotId = state.simSlotId
        )
    }

    override fun fromUiState(
        uiEnabled: Boolean,
        options: List<ConfigurationOption>
    ): BandLockingState {
        val simSlotId = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "simSlotId" }
            ?.value
            ?: 0
        val band = options.filterIsInstance<ConfigurationOption.NumberInput>()
            .find { it.key == "band" }
            ?.value
            ?: NO_BAND_LOCK
        return BandLockingState(
            isEnabled = mapEnabled(uiEnabled),
            band = band,
            simSlotId = simSlotId
        )
    }

    override fun getConfigurationOptions(state: BandLockingState): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = state.simSlotId ?: 0,
            range = 0..2
        ),
        ConfigurationOption.NumberInput(
            key = "band",
            label = "Band",
            value = state.band,
            range = 0..100
        )
    )

    companion object {
        const val NO_BAND_LOCK = -1
    }
}
