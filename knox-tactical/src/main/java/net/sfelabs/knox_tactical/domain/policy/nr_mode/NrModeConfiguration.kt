package net.sfelabs.knox_tactical.domain.policy.nr_mode

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.LteNrMode

data class NrModeConfiguration(
    val mode: LteNrMode,
    val simSlotId: Int? = null,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<NrModeState> {
    override fun toState(currentState: NrModeState): NrModeState {
        return currentState.copy(
            isEnabled = mode != LteNrMode.EnableBothSaAndNsa,
            mode = mode.takeUnless { it == LteNrMode.EnableBothSaAndNsa }
                ?: LteNrMode.DisableNsa,
            simSlotId = simSlotId
        )
    }

    companion object {
        fun disabled(simSlotId: Int? = null) = NrModeConfiguration(
            mode = LteNrMode.EnableBothSaAndNsa,
            simSlotId = simSlotId
        )
    }
}