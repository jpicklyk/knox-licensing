package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.feature.api.BooleanPolicyState
import net.sfelabs.knox.core.feature.api.PolicyComponent
import net.sfelabs.knox.core.feature.api.PolicyGroupingStrategy
import net.sfelabs.knox.core.feature.api.PolicyState
import net.sfelabs.knox.core.feature.api.PolicyStateWrapper
import net.sfelabs.knox.core.feature.domain.model.Policy
import net.sfelabs.knox.core.feature.domain.registry.PolicyRegistry
import net.sfelabs.knox.core.feature.ui.model.ConfigurationOption
import net.sfelabs.knox.core.feature.ui.model.PolicyUiState
import net.sfelabs.knox.core.feature.ui.model.PolicyUiState.ConfigurableToggle
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent
import net.sfelabs.knoxmoduleshowcase.features.policy.model.PolicyGroupUiState
import javax.inject.Inject

@HiltViewModel
class PoliciesViewModel @Inject constructor(
    private val featureRegistry: PolicyRegistry,
    private val groupingStrategy: PolicyGroupingStrategy
) : ViewModel() {
    private val _policies = MutableStateFlow<List<PolicyUiState>>(emptyList())
    val policies = _policies.asStateFlow()

    private val _groupedPolicies = MutableStateFlow<List<PolicyGroupUiState>>(emptyList())
    val groupedPolicies = _groupedPolicies.asStateFlow()

    init {
        loadPolicies()
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            // Load all policies for flat list (backward compatibility)
            val allPolicies = featureRegistry.getAllPolicies()
            _policies.value = allPolicies.mapNotNull { createPolicyUiState(it) }

            // Load grouped policies using the grouping strategy
            val resolvedGroups = groupingStrategy.resolveAllGroups(featureRegistry)
            _groupedPolicies.value = resolvedGroups.map { resolvedGroup ->
                val groupPolicies = resolvedGroup.policies.mapNotNull { component ->
                    // Find the matching policy state
                    allPolicies.find { it.key.policyName == component.policyName }
                        ?.let { createPolicyUiState(it) }
                }
                PolicyGroupUiState(
                    groupId = resolvedGroup.group.id,
                    groupName = resolvedGroup.group.displayName,
                    groupDescription = resolvedGroup.group.description,
                    policies = groupPolicies
                )
            }.filter { it.policies.isNotEmpty() }
        }
    }

    fun onEvent(event: PolicyEvent) {
        when (event) {
            is PolicyEvent.UpdateConfiguration -> {
                viewModelScope.launch {
                    val policy = featureRegistry.getPolicyState(event.featureName) ?: return@launch

                    // Immediately update UI with new state and loading indicator
                    updateUiState(
                        event.featureName,
                        event.newUiState.copyWithLoading(isLoading = true)
                    )

                    val newState = updatePolicyState(policy, event.newUiState)

                    try {
                        when (val result = featureRegistry.setPolicyState(policy.key, newState)) {
                            is ApiResult.Success -> {
                                createPolicyUiState(
                                    Policy(policy.key, PolicyStateWrapper(newState))
                                )?.let { updatedUiState ->
                                    updateUiState(
                                        event.featureName,
                                        updatedUiState
                                    )
                                }
                            }
                            is ApiResult.Error -> {
                                val errorState = event.newUiState.copyWithError(
                                    error = result.apiError.message
                                )
                                updateUiState(event.featureName, errorState)
                            }
                            ApiResult.NotSupported -> {
                                updateUiState(
                                    event.featureName,
                                    event.newUiState.copyWithLoading(isLoading = false)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        val errorState = event.newUiState.copyWithError(
                            error = e.message ?: "Unknown error"
                        )
                        updateUiState(event.featureName, errorState)
                    }
                }
            }
        }
    }

    /**
     * Convert UI state to domain state using polymorphic dispatch through uiConverter.
     *
     * This replaces the previous manual when() dispatch that required instantiating
     * each policy type explicitly.
     */
    @Suppress("UNCHECKED_CAST")
    private fun updatePolicyState(policy: Policy<*>, uiState: PolicyUiState): PolicyState {
        val component = featureRegistry.getComponent(policy.key)
            ?: error("Component not found for policy: ${policy.key.policyName}")

        // Use polymorphic dispatch through uiConverter
        return (component as PolicyComponent<PolicyState>)
            .uiConverter
            .fromUiState(uiState.isEnabled, uiState.currentOptions())
    }

    private fun createPolicyUiState(policy: Policy<*>): PolicyUiState? {
        val component = featureRegistry.getComponent(policy.key) ?: return null
        val state = policy.state.value

        return when (state) {
            is BooleanPolicyState -> PolicyUiState.Toggle(
                title = component.title,
                policyName = component.policyName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message
            )
            else -> ConfigurableToggle(
                title = component.title,
                policyName = component.policyName,
                description = component.description,
                isEnabled = state.isEnabled,
                isSupported = state.isSupported,
                isLoading = false,
                error = state.error?.message,
                configurationOptions = createConfigurationOptions(policy)
            )
        }
    }

    /**
     * Get configuration options using polymorphic dispatch through uiConverter.
     *
     * This replaces the previous manual when() dispatch that required instantiating
     * each policy type explicitly and casting to specific state types.
     */
    @Suppress("UNCHECKED_CAST")
    private fun createConfigurationOptions(policy: Policy<*>): List<ConfigurationOption> {
        val component = featureRegistry.getComponent(policy.key) ?: return emptyList()

        // Use polymorphic dispatch through uiConverter
        return (component as PolicyComponent<PolicyState>)
            .uiConverter
            .getConfigurationOptions(policy.state.value)
    }

    private fun updateUiState(featureName: String, uiState: PolicyUiState) {
        // Update flat list
        _policies.value = _policies.value.map {
            if (it.policyName == featureName) uiState else it
        }

        // Update grouped list
        _groupedPolicies.value = _groupedPolicies.value.map { group ->
            group.copy(
                policies = group.policies.map { policy ->
                    if (policy.policyName == featureName) uiState else policy
                }
            )
        }
    }
}