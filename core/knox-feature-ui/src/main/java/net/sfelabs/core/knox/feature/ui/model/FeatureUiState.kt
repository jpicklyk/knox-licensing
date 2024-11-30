package net.sfelabs.core.knox.feature.ui.model

sealed class FeatureUiState {
    abstract val name: String
    abstract val description: String
    abstract val isLoading: Boolean
    abstract val error: String?

    data class Toggle(
        override val name: String,
        override val description: String,
        val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null
    ) : FeatureUiState()
}