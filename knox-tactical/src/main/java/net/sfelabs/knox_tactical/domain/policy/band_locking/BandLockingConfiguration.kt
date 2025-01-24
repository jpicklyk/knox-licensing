package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

data class BandLockingConfiguration(
    val band: Int,
    val simSlotId: Int? = null,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BandLockingState> {

    override fun toState(currentState: BandLockingState): BandLockingState {
        return currentState.copy(
            isEnabled = band != NO_BAND_LOCK,
            band = band,
            simSlotId = simSlotId
        )
    }

    override fun toConfigurationOptions(): List<ConfigurationOption> = listOf(
        ConfigurationOption.NumberInput(
            key = "simSlotId",
            label = "SIM Slot Id",
            value = simSlotId ?: 0,
            range = 0..2
        ),
        ConfigurationOption.NumberInput(
            key = "band",
            label = "Band",
            value = band,
            range = 0..100
        )
    )

    override fun fromConfigurationOptions(
        options: List<ConfigurationOption>,
        enabled: Boolean
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
            isEnabled = mapEnabled(enabled),
            band = band,
            simSlotId = simSlotId
        )
    }

    companion object {
        const val NO_BAND_LOCK = -1

        fun disabled(simSlotId: Int? = null) = BandLockingConfiguration(
            band = NO_BAND_LOCK,
            simSlotId = simSlotId
        )
    }
}
