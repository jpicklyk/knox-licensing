package net.sfelabs.knoxmoduleshowcase.features.policy.model

import net.sfelabs.knox.core.feature.ui.model.PolicyUiState

/**
 * UI state for a group of policies.
 */
data class PolicyGroupUiState(
    val groupId: String,
    val groupName: String,
    val groupDescription: String = "",
    val policies: List<PolicyUiState>,
    val isExpanded: Boolean = true
) {
    val isEmpty: Boolean get() = policies.isEmpty()
    val size: Int get() = policies.size
}
