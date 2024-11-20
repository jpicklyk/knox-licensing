package net.sfelabs.core.knoxfeature.domain.usecase.factory

import net.sfelabs.core.knoxfeature.domain.model.FeatureKey
import net.sfelabs.core.knoxfeature.domain.usecase.base.FeatureUseCase
import kotlin.reflect.KClass

object FeatureUseCaseFactory {
    private val useCases = mutableMapOf<KClass<out FeatureKey<*>>, FeatureUseCase<*, *, *>>()

    fun <T, P, R : Any> register(
        feature: KClass<out FeatureKey<T>>,
        useCase: FeatureUseCase<T, P, R>
    ) {
        useCases[feature] = useCase
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, P, R : Any> getUseCase(feature: FeatureKey<T>): FeatureUseCase<T, P, R> {
        return useCases[feature::class] as? FeatureUseCase<T, P, R>
            ?: throw IllegalArgumentException("No use case found for feature: ${feature.featureName}")
    }

    fun clearUseCases() {
        useCases.clear()
    }
}