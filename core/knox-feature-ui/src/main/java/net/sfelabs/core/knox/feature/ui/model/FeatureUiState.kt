package net.sfelabs.core.knox.feature.ui.model

sealed class FeatureUiState {
    abstract val title: String
    abstract val featureName: String
    abstract val description: String
    abstract val isLoading: Boolean
    abstract val error: String?

    data class Toggle(
        override val title: String,
        override val featureName: String,
        override val description: String,
        val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null
    ) : FeatureUiState()

    data class ConfigurableToggle(
        override val title: String,
        override val featureName: String,
        override val description: String,
        val isEnabled: Boolean,
        val configurationOptions: Map<String, Any>,
        override val isLoading: Boolean = false,
        override val error: String? = null
    ) : FeatureUiState()
}