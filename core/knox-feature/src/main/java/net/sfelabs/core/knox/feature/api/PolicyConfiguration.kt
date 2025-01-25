package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

interface PolicyConfiguration<T : PolicyState, D: Any> {
    val stateMapping: StateMapping

    /**
     * Transform raw API data into a PolicyState, applying state mapping
     */
    fun fromApiData(apiData: D): T

    /**
     * Transform PolicyState into API data, applying state mapping
     */
    fun toApiData(state: T): D

    /**
     * Convert UI state to PolicyState
     */
    fun fromUiState(uiEnabled: Boolean, options: List<ConfigurationOption>): T

//    /**
//     * Convert PolicyState to UI configuration options
//     */
//    fun toUiState(state: T, component: FeatureComponent<T>): PolicyUiState {
//        return PolicyUiState.ConfigurableToggle(
//            title = component.title,
//            featureName = component.featureName,
//            description = component.description,
//            isEnabled = state.isEnabled,
//            isSupported = state.isSupported,
//            error = state.error?.message,
//            configurationOptions = getConfigurationOptions(state)
//        )
//    }

    /**
     * Maps between API and domain state based on stateMapping
     */
    fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
        StateMapping.DIRECT -> enabled
        StateMapping.INVERTED -> !enabled
    }

    fun getConfigurationOptions(state: T): List<ConfigurationOption>
}