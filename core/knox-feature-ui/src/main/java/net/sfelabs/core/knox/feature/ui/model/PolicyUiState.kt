package net.sfelabs.core.knox.feature.ui.model

sealed class PolicyUiState {
    abstract val isSupported: Boolean
    abstract val title: String
    abstract val featureName: String
    abstract val description: String
    abstract val isLoading: Boolean
    abstract val error: String?
    abstract val isEnabled: Boolean

    data class Toggle(
        override val isSupported: Boolean,
        override val title: String,
        override val featureName: String,
        override val description: String,
        override val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null,
    ) : PolicyUiState()

    data class ConfigurableToggle(
        override val isSupported: Boolean,
        override val title: String,
        override val featureName: String,
        override val description: String,
        override val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null,
        val configurationOptions: List<ConfigurationOption>
    ) : PolicyUiState()
}