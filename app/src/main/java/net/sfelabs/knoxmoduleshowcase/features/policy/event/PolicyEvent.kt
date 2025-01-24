package net.sfelabs.knoxmoduleshowcase.features.policy.event

import net.sfelabs.core.knox.feature.ui.model.PolicyUiState

sealed interface PolicyEvent {
    data class UpdateConfiguration(
        val featureName: String,
        val newUiState: PolicyUiState
    ) : PolicyEvent

}