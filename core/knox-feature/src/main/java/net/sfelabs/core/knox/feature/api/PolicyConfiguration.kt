package net.sfelabs.core.knox.feature.api

interface PolicyConfiguration<T : PolicyState> {
    val stateMapping: StateMapping

    /**
     * Transform the configuration into a PolicyState, applying any state mapping
     */
    fun toState(currentState: T): T

    /**
     * Maps the enabled state according to the configuration's state mapping
     */
    fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
        StateMapping.DIRECT -> enabled
        StateMapping.INVERTED -> !enabled
    }
}