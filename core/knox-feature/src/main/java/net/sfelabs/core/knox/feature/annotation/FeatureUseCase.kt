package net.sfelabs.core.knox.feature.annotation

import net.sfelabs.core.knox.feature.domain.model.FeatureCategory
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class FeatureUseCase(
    val featureName: String,
    val type: Type,
    val category: FeatureCategory = FeatureCategory.PRODUCTION,
    val config: KClass<*>
) {
    enum class Type {
        GETTER,
        SETTER
    }
}
