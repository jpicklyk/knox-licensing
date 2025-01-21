package net.sfelabs.knox_tactical.domain.policy.band_locking

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping

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

    companion object {
        const val NO_BAND_LOCK = -1

        fun disabled(simSlotId: Int? = null) = BandLockingConfiguration(
            band = NO_BAND_LOCK,
            simSlotId = simSlotId
        )
    }
}
