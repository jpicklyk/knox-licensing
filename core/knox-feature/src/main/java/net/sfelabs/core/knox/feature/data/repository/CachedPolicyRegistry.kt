package net.sfelabs.core.knox.feature.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureComponent
import net.sfelabs.core.knox.feature.api.FeatureKey
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.api.PolicyStateWrapper
import net.sfelabs.core.knox.feature.domain.model.Feature
import net.sfelabs.core.knox.feature.domain.registry.FeatureRegistry

class CachedPolicyRegistry(private val delegate: DefaultFeatureRegistry) : FeatureRegistry {
    private val cache = mutableMapOf<String, Feature<PolicyState>>()
    private val mutex = Mutex()

    var components: Set<FeatureComponent<out PolicyState>>
        get() = delegate.components
        set(value) {
            delegate.components = value
            cache.clear()
        }

    suspend fun getFeature(featureName: String, forceRefresh: Boolean = false): Feature<PolicyState>? {
        return mutex.withLock {
            when {
                forceRefresh -> delegate.getPolicyState(featureName)?.also { cache[featureName] = it }
                featureName in cache -> cache[featureName]
                else -> delegate.getPolicyState(featureName)?.also { cache[featureName] = it }
            }
        }
    }

    override suspend fun getAllFeatures(): List<Feature<*>> = mutex.withLock {
        delegate.getAllFeatures().also { features ->
            cache.clear()
            features.forEach {
                cache[it.key.featureName] = it
            }
        }
    }

    suspend fun clearCache() = mutex.withLock { cache.clear() }

    override suspend fun getPolicyState(featureName: String) = getFeature(featureName, false)
    override suspend fun <T : PolicyState> setPolicyState(
        featureKey: FeatureKey<T>,
        state: T
    ): ApiResult<Unit> {
        return mutex.withLock {
            getHandler(featureKey)?.setState(state)?.also { result ->
                if (result is ApiResult.Success) {
                    val cachedFeature = cache[featureKey.featureName]
                    if (cachedFeature != null) {
                        cache[featureKey.featureName] = Feature(
                            key = cachedFeature.key,
                            state = PolicyStateWrapper(state)
                        )
                    }
                }
            } ?: ApiResult.Error(DefaultApiError.UnexpectedError("Policy handler not found"))
        }
    }

    override fun getComponent(key: FeatureKey<*>) = delegate.getComponent(key)
    override fun <T : PolicyState> getHandler(key: FeatureKey<T>) = delegate.getHandler(key)
    override suspend fun getFeatures(category: FeatureCategory) = delegate.getFeatures(category)
    override fun isRegistered(key: FeatureKey<*>) = delegate.isRegistered(key)
}