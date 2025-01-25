package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

/**
 * Interface for converting between policy domain states and UI states.
 *
 * This interface defines the contract for how policies translate their domain state to UI
 * representations and back. It bridges the domain and presentation layers by providing common
 * methods for:
 * - Converting UI state updates to domain state
 * - Getting UI configuration options from domain state
 *
 * Implementations handle the specifics of each policy type:
 * - BooleanPolicy: Simple enabled/disabled state with no configuration
 * - ConfigurableStatePolicy: Complex state with additional configuration options
 *
 * @param T The PolicyState type this converter handles
 */
interface PolicyUiConverter<T : PolicyState> {
    /**
     * Convert UI state to domain state.
     *
     * @param uiEnabled The enabled state from the UI
     * @param options The current UI configuration options
     * @return A new PolicyState instance reflecting the UI state
     */
    fun fromUiState(uiEnabled: Boolean, options: List<ConfigurationOption>): T

    /**
     * Get the configuration options for the UI based on current domain state.
     *
     * @param state The current domain state
     * @return List of configuration options for the UI, empty if no configuration is needed
     */
    fun getConfigurationOptions(state: T): List<ConfigurationOption>
}