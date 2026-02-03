package net.sfelabs.knoxmoduleshowcase.features.policy.model

import net.sfelabs.knox.core.feature.api.PolicyCapability

/**
 * Complete UI state for the Policy screen.
 *
 * Holds state for tabs, search, groups, and expansion state.
 */
data class PolicyScreenState(
    val selectedTab: SdkSource = SdkSource.TACTICAL,
    val searchQuery: String = "",
    val tacticalGroups: List<PolicyGroupUiState> = emptyList(),
    val enterpriseGroups: List<PolicyGroupUiState> = emptyList(),
    /** Groups that are expanded. Empty set means all groups are collapsed. */
    val expandedGroupIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    /** Maps policy name to its capabilities for search filtering */
    val policyCapabilities: Map<String, Set<PolicyCapability>> = emptyMap()
) {
    /**
     * Total count of tactical policies across all groups.
     */
    val tacticalPolicyCount: Int
        get() = tacticalGroups.sumOf { it.size }

    /**
     * Total count of enterprise policies across all groups.
     */
    val enterprisePolicyCount: Int
        get() = enterpriseGroups.sumOf { it.size }

    /**
     * Filtered count of tactical policies (matches search query).
     * Returns total count when not searching.
     */
    val filteredTacticalPolicyCount: Int
        get() = if (searchQuery.isBlank()) {
            tacticalPolicyCount
        } else {
            filterGroupsByQuery(tacticalGroups).sumOf { it.size }
        }

    /**
     * Filtered count of enterprise policies (matches search query).
     * Returns total count when not searching.
     */
    val filteredEnterprisePolicyCount: Int
        get() = if (searchQuery.isBlank()) {
            enterprisePolicyCount
        } else {
            filterGroupsByQuery(enterpriseGroups).sumOf { it.size }
        }

    /**
     * Helper to filter groups by search query.
     */
    private fun filterGroupsByQuery(groups: List<PolicyGroupUiState>): List<PolicyGroupUiState> {
        val query = searchQuery.trim().lowercase()
        return groups.mapNotNull { group ->
            val filteredPolicies = group.policies.filter { policy ->
                policy.title.lowercase().contains(query) ||
                    policy.description.lowercase().contains(query) ||
                    policy.policyName.lowercase().contains(query) ||
                    matchesCapability(policy.policyName, query)
            }
            if (filteredPolicies.isEmpty()) null
            else group.copy(policies = filteredPolicies)
        }
    }

    /**
     * Returns groups for the currently selected tab with expansion state applied.
     * Groups are collapsed by default until the user expands them.
     */
    val currentTabGroups: List<PolicyGroupUiState>
        get() {
            val groups = when (selectedTab) {
                SdkSource.TACTICAL -> tacticalGroups
                SdkSource.ENTERPRISE -> enterpriseGroups
            }
            return groups.map { group ->
                group.copy(isExpanded = group.groupId in expandedGroupIds)
            }
        }

    /**
     * Returns groups filtered by the search query.
     * Searches title, description, policy name, and capabilities.
     * When searching, groups are auto-expanded to show matches.
     */
    fun filteredGroups(): List<PolicyGroupUiState> {
        val groups = currentTabGroups

        if (searchQuery.isBlank()) return groups

        val query = searchQuery.trim().lowercase()
        return groups.mapNotNull { group ->
            val filteredPolicies = group.policies.filter { policy ->
                // Search in title, description, and policy name
                policy.title.lowercase().contains(query) ||
                    policy.description.lowercase().contains(query) ||
                    policy.policyName.lowercase().contains(query) ||
                    // Search in capabilities (e.g., "stig", "radio", "bluetooth")
                    matchesCapability(policy.policyName, query)
            }
            if (filteredPolicies.isEmpty()) {
                null
            } else {
                // Auto-expand groups when searching
                group.copy(policies = filteredPolicies, isExpanded = true)
            }
        }
    }

    /**
     * Check if any capability of the policy matches the search query.
     */
    private fun matchesCapability(policyName: String, query: String): Boolean {
        val capabilities = policyCapabilities[policyName] ?: return false
        return capabilities.any { capability ->
            // Match against capability name (e.g., "STIG" -> "stig", "MODIFIES_RADIO" -> "radio")
            capability.name.lowercase().contains(query) ||
                capability.name.lowercase().replace("_", " ").contains(query)
        }
    }
}
