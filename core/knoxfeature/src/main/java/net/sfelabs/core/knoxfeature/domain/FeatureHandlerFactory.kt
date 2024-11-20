package net.sfelabs.core.knoxfeature.domain

import net.sfelabs.core.knoxfeature.domain.model.FeatureKey
import javax.inject.Inject
import kotlin.reflect.KClass

class FeatureHandlerFactory @Inject constructor(
    private val handlers: Map<KClass<out Any>, Any>
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> getHandler(feature: FeatureKey<T>): FeatureHandler<T> {
        return handlers[feature::class] as? FeatureHandler<T>
            ?: throw IllegalArgumentException("No handler found for feature: ${feature.featureName}")
    }
}