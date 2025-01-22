package net.sfelabs.knoxmoduleshowcase.features.policy.event

import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

sealed interface PolicyEvent {
    // For handling basic enable/disable toggle
    data class UpdateEnabled(
        val featureName: String,
        val isEnabled: Boolean
    ) : PolicyEvent

    // For handling configuration changes
    data class UpdateConfiguration(
        val featureName: String,
        val key: String,
        val value: Any
    ) : PolicyEvent

    // For handling bulk configuration updates (when using Save button)
    data class SaveConfiguration(
        val featureName: String,
        val configurations: List<ConfigurationOption>
    ) : PolicyEvent
}