package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

// Base class for complex policies
abstract class ConfigurableStatePolicy<T : PolicyState, D : Any, C : PolicyConfiguration<T, D>>(
    protected val stateMapping: StateMapping = StateMapping.DIRECT
) : PolicyContract<T>, PolicyUiConverter<T> {
    // Each configurable policy must provide its configuration
    protected abstract val configuration: C

    /**
     * Convert UI state to domain state.
     *
     * @param uiEnabled The enabled state from the UI
     * @param options The current UI configuration options
     * @return A new PolicyState instance reflecting the UI state
     */
    override fun fromUiState(uiEnabled: Boolean, options: List<ConfigurationOption>): T =
        configuration.fromUiState(uiEnabled, options)

    /**
     * Get the configuration options for the UI based on current domain state.
     *
     * @param state The current domain state
     * @return List of configuration options for the UI, empty if no configuration is needed
     */
    override fun getConfigurationOptions(state: T): List<ConfigurationOption> =
        configuration.getConfigurationOptions(state)
}