package net.sfelabs.core.knox.feature.ui.model

sealed class PolicyUiState {
    abstract val isSupported: Boolean
    abstract val title: String
    abstract val featureName: String
    abstract val description: String
    abstract val isLoading: Boolean
    abstract val error: String?
    abstract val isEnabled: Boolean

    abstract fun copyWithError(error: String?): PolicyUiState
    abstract fun copyWithLoading(isLoading: Boolean): PolicyUiState

    data class Toggle(
        override val isSupported: Boolean,
        override val title: String,
        override val featureName: String,
        override val description: String,
        override val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null,
    ) : PolicyUiState() {
        override fun copyWithError(error: String?) = copy(isLoading = false, error = error)
        override fun copyWithLoading(isLoading: Boolean) = copy(isLoading = isLoading)
    }

    data class ConfigurableToggle(
        override val isSupported: Boolean,
        override val title: String,
        override val featureName: String,
        override val description: String,
        override val isEnabled: Boolean,
        override val isLoading: Boolean = false,
        override val error: String? = null,
        val configurationOptions: List<ConfigurationOption>
    ) : PolicyUiState() {
        override fun copyWithError(error: String?) = copy(isLoading = false, error = error)
        override fun copyWithLoading(isLoading: Boolean) = copy(isLoading = isLoading)
    }

    fun currentOptions(): List<ConfigurationOption> {
        return if (this is Toggle) {
            emptyList()
        } else {
            (this as ConfigurableToggle).configurationOptions
        }
    }
}