package net.sfelabs.core.domain.api.feature

import net.sfelabs.core.domain.api.ApiResult
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.reflect.KClass

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FeatureHandlerKey(val value: KClass<out FeatureKey<*>>)

interface FeatureHandler<T> {
    suspend fun getState(): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}

class FeatureHandlerFactory @Inject constructor(
    private val handlers: Map<KClass<out FeatureKey<*>>, @JvmSuppressWildcards FeatureHandler<*>>
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> getHandler(feature: FeatureKey<T>): FeatureHandler<T> {
        return handlers[feature::class] as? FeatureHandler<T>
            ?: throw IllegalArgumentException("No handler found for feature: ${feature.featureName}")
    }
}