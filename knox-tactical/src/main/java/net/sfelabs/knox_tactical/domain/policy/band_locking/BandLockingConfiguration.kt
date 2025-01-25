package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class BandLockingConfiguration(
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BandLockingState, BandLockingState> {

    override fun fromApiData(apiData: BandLockingState): BandLockingState {
        return apiData.copy(
            isEnabled = mapEnabled(apiData.band != NO_BAND_LOCK),
            band = apiData.band,
            simSlotId = apiData.simSlotId
        )
    }

    override fun toApiData(state: BandLockingState): BandLockingState {
        return state.copy(
            isEnabled = mapEnabled(state.isEnabled),
            band = state.band,
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
