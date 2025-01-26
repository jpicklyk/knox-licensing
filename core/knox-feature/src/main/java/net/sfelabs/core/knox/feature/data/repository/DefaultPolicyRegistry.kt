package net.sfelabs.core.knox.feature.data.repository

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.domain.usecase.handler.PolicyHandler
import net.sfelabs.core.knox.feature.domain.model.Policy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyComponent
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper
import net.sfelabs.core.knox.feature.domain.registry.PolicyRegistry

class DefaultPolicyRegistry : PolicyRegistry {
    var components: Set<PolicyComponent<out PolicyState>> = emptySet()

    private val componentsByName: Map<String, PolicyComponent<out PolicyState>> by lazy {
        components.associateBy { it.policyName }
    }

    override fun getComponent(key: PolicyKey<*>): PolicyComponent<out PolicyState>? {
        return componentsByName[key.policyName]
    }

    override fun <T : PolicyState> getHandler(key: PolicyKey<T>): PolicyHandler<T>? {
        val component = componentsByName[key.policyName] ?: return null

        return if (component.key::class == key::class) {
            @Suppress("UNCHECKED_CAST")
            component.handler as? PolicyHandler<T>
        } else {
            null
        }
    }

    override suspend fun getAllPolicies(): List<Policy<*>> {
        return components.map { component ->
            @Suppress("UNCHECKED_CAST")
            val typedComponent = component
            val handler = typedComponent.handler
            Policy(
                key = typedComponent.key,
                state = PolicyStateWrapper(handler.getState())
            )
        }
    }

    override suspend fun getPolicies(category: PolicyCategory): List<Policy<*>> {
        return components
            .filter { it.category == category }
            .map { component ->
                @Suppress("UNCHECKED_CAST")
                val typedComponent = component
                val handler = typedComponent.handler
                Policy(
                    key = typedComponent.key,
                    state = PolicyStateWrapper(handler.getState())
                )
            }
    }

    override fun isRegistered(key: PolicyKey<*>): Boolean {
        return componentsByName.containsKey(key.policyName)
    }

    override suspend fun getPolicyState(featureName: String): Policy<PolicyState>? {
        val component = componentsByName[featureName] ?: return null
        @Suppress("UNCHECKED_CAST")
        val handler = component.handler
        return Policy(
            key = component.key,
            state = PolicyStateWrapper(handler.getState())
        )
    }

    override suspend fun <T : PolicyState> setPolicyState(
        policyKey: PolicyKey<T>,
        state: T
    ): ApiResult<Unit> {
        return getHandler(policyKey)?.setState(state)
            ?: ApiResult.Error(DefaultApiError.UnexpectedError("Policy handler not found"))
    }
}