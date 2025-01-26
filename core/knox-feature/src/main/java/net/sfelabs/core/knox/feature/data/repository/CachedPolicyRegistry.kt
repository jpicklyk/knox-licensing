package net.sfelabs.core.knox.feature.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyComponent
import net.sfelabs.core.knox.feature.api.PolicyKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper
import net.sfelabs.core.knox.feature.domain.model.Policy
import net.sfelabs.core.knox.feature.domain.registry.PolicyRegistry

class CachedPolicyRegistry(private val delegate: DefaultPolicyRegistry) : PolicyRegistry {
    private val cache = mutableMapOf<String, Policy<PolicyState>>()
    private val mutex = Mutex()

    var components: Set<PolicyComponent<out PolicyState>>
        get() = delegate.components
        set(value) {
            delegate.components = value
            cache.clear()
        }

    suspend fun getFeature(featureName: String, forceRefresh: Boolean = false): Policy<PolicyState>? {
        return mutex.withLock {
            when {
                forceRefresh -> delegate.getPolicyState(featureName)?.also { cache[featureName] = it }
                featureName in cache -> cache[featureName]
                else -> delegate.getPolicyState(featureName)?.also { cache[featureName] = it }
            }
        }
    }

    override suspend fun getAllPolicies(): List<Policy<*>> = mutex.withLock {
        delegate.getAllPolicies().also { features ->
            cache.clear()
            features.forEach {
                cache[it.key.policyName] = it
            }
        }
    }

    suspend fun clearCache() = mutex.withLock { cache.clear() }

    override suspend fun getPolicyState(featureName: String) = getFeature(featureName, false)
    override suspend fun <T : PolicyState> setPolicyState(
        policyKey: PolicyKey<T>,
        state: T
    ): ApiResult<Unit> {
        return mutex.withLock {
            getHandler(policyKey)?.setState(state)?.also { result ->
                if (result is ApiResult.Success) {
                    val cachedFeature = cache[policyKey.policyName]
                    if (cachedFeature != null) {
                        cache[policyKey.policyName] = Policy(
                            key = cachedFeature.key,
                            state = PolicyStateWrapper(state)
                        )
                    }
                }
            } ?: ApiResult.Error(DefaultApiError.UnexpectedError("Policy handler not found"))
        }
    }

    override fun getComponent(key: PolicyKey<*>) = delegate.getComponent(key)
    override fun <T : PolicyState> getHandler(key: PolicyKey<T>) = delegate.getHandler(key)
    override suspend fun getPolicies(category: PolicyCategory) = delegate.getPolicies(category)
    override fun isRegistered(key: PolicyKey<*>) = delegate.isRegistered(key)
}