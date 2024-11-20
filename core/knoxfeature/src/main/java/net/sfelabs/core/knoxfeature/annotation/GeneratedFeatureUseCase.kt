package net.sfelabs.core.knoxfeature.annotation

import net.sfelabs.core.knoxfeature.domain.model.FeatureKey
import net.sfelabs.core.knoxfeature.domain.FeatureCategory
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class GeneratedFeatureUseCase(
    val feature: KClass<out FeatureKey<*>>,
    val category: FeatureCategory = FeatureCategory.PRODUCTION,
    val defaultBlocking: Boolean = false
)
