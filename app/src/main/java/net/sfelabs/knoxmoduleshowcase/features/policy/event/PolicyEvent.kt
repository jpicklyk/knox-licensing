package net.sfelabs.knoxmoduleshowcase.features.policy.event

import net.sfelabs.knox.core.feature.ui.model.PolicyUiState
import net.sfelabs.knoxmoduleshowcase.features.policy.model.SdkSource

sealed interface PolicyEvent {
    data class UpdateConfiguration(
        val featureName: String,
        val newUiState: PolicyUiState
    ) : PolicyEvent

    /** Switch between TE Policies and Base Policies tabs */
    data class SelectTab(val tab: SdkSource) : PolicyEvent

    /** Update the search query for filtering policies */
    data class UpdateSearchQuery(val query: String) : PolicyEvent

    /** Toggle expansion state of a policy group */
    data class ToggleGroupExpansion(val groupId: String) : PolicyEvent
}