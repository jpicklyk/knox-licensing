package net.sfelabs.knox_tactical.domain.policy.night_vision

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping

data class NightVisionConfiguration(
    val enabled: Boolean,
    val useRedOverlay: Boolean,
    override val stateMapping: StateMapping
) : PolicyConfiguration<NightVisionState> {
    override fun toState(currentState: NightVisionState): NightVisionState {
        return currentState.copy(
            isEnabled = mapEnabled(enabled),
            useRedOverlay = useRedOverlay
        )
    }
}