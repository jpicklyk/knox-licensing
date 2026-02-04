package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import net.sfelabs.knoxmoduleshowcase.features.policy.model.PolicyScreenState
import net.sfelabs.knoxmoduleshowcase.features.policy.model.SdkSource
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

    private val _screenState = MutableStateFlow(PolicyScreenState(isLoading = true))
    val screenState = _screenState.asStateFlow()

    init {
        loadPolicies()
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            // Get all components and separate by SDK source
            val allComponents = featureRegistry.getAllComponents()
            val tacticalComponents = allComponents.filter {
                SdkSource.fromComponent(it) == SdkSource.TACTICAL
            }.toSet()
            val enterpriseComponents = allComponents.filter {
                SdkSource.fromComponent(it) == SdkSource.ENTERPRISE
            }.toSet()

            // Build capabilities map for search filtering
            val capabilitiesMap = allComponents.associate { component ->
                component.policyName to component.capabilities
            }

            // Load all policies
            val allPolicies = featureRegistry.getAllPolicies()
            _policies.value = allPolicies.mapNotNull { createPolicyUiState(it) }

            // Create grouped policies for each SDK
            val tacticalGroups = createGroupedPolicies(tacticalComponents, allPolicies)
            val enterpriseGroups = createGroupedPolicies(enterpriseComponents, allPolicies)

            // All groups start collapsed (expandedGroupIds is empty)
            _screenState.value = PolicyScreenState(
                tacticalGroups = tacticalGroups,
                enterpriseGroups = enterpriseGroups,
                expandedGroupIds = emptySet(),
                policyCapabilities = capabilitiesMap,
                isLoading = false
            )

            // For backward compatibility
            _groupedPolicies.value = tacticalGroups + enterpriseGroups
        }
    }

    private fun createGroupedPolicies(
        components: Set<PolicyComponent<out PolicyState>>,
        allPolicies: List<Policy<PolicyState>>
    ): List<PolicyGroupUiState> {
        return groupingStrategy.getGroups().mapNotNull { group ->
            val groupComponents = components.filter {
                groupingStrategy.getGroupForPolicy(it)?.id == group.id
            }
            if (groupComponents.isEmpty()) return@mapNotNull null

            val groupPolicies = groupComponents.mapNotNull { component ->
                allPolicies.find { it.key.policyName == component.policyName }
                    ?.let { createPolicyUiState(it) }
            }

            if (groupPolicies.isEmpty()) return@mapNotNull null

            PolicyGroupUiState(
                groupId = group.id,
                groupName = group.displayName,
                groupDescription = group.description,
                policies = groupPolicies
            )
        }
    }

    fun onEvent(event: PolicyEvent) {
        when (event) {
            is PolicyEvent.SelectTab -> {
                _screenState.update { it.copy(selectedTab = event.tab) }
            }

            is PolicyEvent.UpdateSearchQuery -> {
                _screenState.update { it.copy(searchQuery = event.query) }
            }

            is PolicyEvent.ToggleGroupExpansion -> {
                _screenState.update { state ->
                    // Toggle: if expanded, remove from set (collapse); if collapsed, add to set (expand)
                    val newExpanded = if (event.groupId in state.expandedGroupIds) {
                        state.expandedGroupIds - event.groupId
                    } else {
                        state.expandedGroupIds + event.groupId
                    }
                    state.copy(expandedGroupIds = newExpanded)
                }
            }

            is PolicyEvent.UpdateConfiguration -> {
                viewModelScope.launch {
                    val policy = featureRegistry.getPolicyState(event.featureName) ?: return@launch

                    // Capture original UI state before optimistic update for rollback on error
                    val originalUiState = _policies.value.find { it.policyName == event.featureName }
                        ?: return@launch

                    // Immediately update UI with new state and loading indicator
                    updateUiState(
                        event.featureName,
                        event.newUiState.copyWithLoading(isLoading = true)
                    )

                    val newState = updatePolicyState(policy, event.newUiState)

                    try {
                        when (val result = featureRegistry.setPolicyState(policy.key, newState)) {
                            is ApiResult.Success -> {
                                // IMPORTANT: Always refresh state from device after successful update.
                                // The state from fromUiState() only contains fields derived from UI options
                                // and loses non-UI state fields (e.g., HdmState.supportedMask).
                                // Refreshing via getPolicyState() calls getState() which returns the
                                // complete state including device-derived fields.
                                // See: PolicyConfiguration.fromUiState() design limitation in knox-core.
                                val refreshedPolicy = featureRegistry.getPolicyState(event.featureName)
                                if (refreshedPolicy != null) {
                                    createPolicyUiState(refreshedPolicy)?.let { updatedUiState ->
                                        updateUiState(event.featureName, updatedUiState)
                                    }
                                } else {
                                    // Fallback to local state if refresh fails
                                    createPolicyUiState(
                                        Policy(policy.key, PolicyStateWrapper(newState))
                                    )?.let { updatedUiState ->
                                        updateUiState(event.featureName, updatedUiState)
                                    }
                                }
                            }
                            is ApiResult.Error -> {
                                // Revert to original state but show the error
                                val errorState = originalUiState.copyWithError(
                                    error = result.apiError.message
                                )
                                updateUiState(event.featureName, errorState)
                            }
                            ApiResult.NotSupported -> {
                                // Revert to original state
                                updateUiState(
                                    event.featureName,
                                    originalUiState.copyWithLoading(isLoading = false)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // Revert to original state but show the error
                        val errorState = originalUiState.copyWithError(
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

        // Update grouped list (backward compatibility)
        _groupedPolicies.value = _groupedPolicies.value.map { group ->
            group.copy(
                policies = group.policies.map { policy ->
                    if (policy.policyName == featureName) uiState else policy
                }
            )
        }

        // Update screen state groups
        _screenState.update { state ->
            state.copy(
                tacticalGroups = state.tacticalGroups.map { group ->
                    group.copy(
                        policies = group.policies.map { policy ->
                            if (policy.policyName == featureName) uiState else policy
                        }
                    )
                },
                enterpriseGroups = state.enterpriseGroups.map { group ->
                    group.copy(
                        policies = group.policies.map { policy ->
                            if (policy.policyName == featureName) uiState else policy
                        }
                    )
                }
            )
        }
    }
}