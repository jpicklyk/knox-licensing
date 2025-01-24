package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

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

    /**
     * Convert the configuration components to a list of ConfigurationOptions for the UI layer
     */
    fun toConfigurationOptions(): List<ConfigurationOption>

    /**
     * Convert a list of ConfigurationOptions to a PolicyState, applying any state mapping
     */
    fun fromConfigurationOptions(options: List<ConfigurationOption>, enabled: Boolean): T
}