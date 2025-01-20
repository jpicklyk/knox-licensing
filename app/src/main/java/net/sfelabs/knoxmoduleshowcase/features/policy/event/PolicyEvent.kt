package net.sfelabs.knoxmoduleshowcase.features.policy.event

import net.sfelabs.core.knox.feature.api.PolicyState

sealed interface PolicyEvent {
    data class UpdatePolicy(
        val policyState: PolicyState,
        val featureName: String
    ) : PolicyEvent
}