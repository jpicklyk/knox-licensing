package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.knox.feature.api.PolicyConfiguration

data class HdmConfiguration(
    val components: Set<HdmComponent>
) : PolicyConfiguration<HdmState> {
    override fun toState(currentState: HdmState): HdmState {
        val newMask = components.fold(0) { acc, component ->
            acc or component.mask
        }
        return currentState.copy(policyMask = newMask)
    }
}