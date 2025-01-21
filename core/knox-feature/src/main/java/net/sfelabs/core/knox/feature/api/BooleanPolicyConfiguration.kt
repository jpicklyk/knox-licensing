package net.sfelabs.core.knox.feature.api

data class BooleanPolicyConfiguration(
    val enabled: Boolean,
    override val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyConfiguration<BooleanPolicyState> {
    override fun toState(currentState: BooleanPolicyState): BooleanPolicyState {
        val mappedEnabled = when (stateMapping) {
            StateMapping.DIRECT -> enabled
            StateMapping.INVERTED -> !enabled
        }
        return currentState.copy(isEnabled = mappedEnabled)
    }

    companion object {
        fun fromState(state: BooleanPolicyState, stateMapping: StateMapping): BooleanPolicyConfiguration {
            return BooleanPolicyConfiguration(
                enabled = state.isEnabled, // The mapping will be applied in toState
                stateMapping = stateMapping
            )
        }
    }
}
