package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping

data class ImsConfiguration(
    val simSlotId: Int,
    val enabled: Boolean,
    override val stateMapping: StateMapping
) : PolicyConfiguration<ImsState> {
    override fun toState(currentState: ImsState): ImsState {
        return currentState.copy(
            isEnabled = mapEnabled(enabled),
            simSlotId = simSlotId
        )
    }
}