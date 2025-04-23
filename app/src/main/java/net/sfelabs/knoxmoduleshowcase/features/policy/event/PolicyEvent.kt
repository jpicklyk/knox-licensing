package net.sfelabs.knoxmoduleshowcase.features.policy.event

import net.sfelabs.knox.core.feature.ui.model.PolicyUiState

sealed interface PolicyEvent {
    data class UpdateConfiguration(
        val featureName: String,
        val newUiState: PolicyUiState
    ) : PolicyEvent

}