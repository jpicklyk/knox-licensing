package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.knox.feature.api.PolicyConfiguration
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

data class AutoCallPickupConfiguration(
    val mode: AutoCallPickupMode,
    override val stateMapping: StateMapping = StateMapping.DIRECT
): PolicyConfiguration<AutoCallPickupState> {
    override fun toState(currentState: AutoCallPickupState): AutoCallPickupState {
        // If the mode is Disable, ensure isEnabled is false
        return currentState.copy(
            isEnabled = mode != AutoCallPickupMode.Disable,
            mode = mode.takeUnless { it == AutoCallPickupMode.Disable }
                ?: AutoCallPickupMode.EnableAlwaysAccept
        )
    }
}
