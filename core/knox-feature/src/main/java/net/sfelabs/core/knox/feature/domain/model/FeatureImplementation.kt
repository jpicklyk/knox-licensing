package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

interface FeatureImplementation<T : Any> {
    val featureName: String
    val category: FeatureCategory
    val description: String
    val getter: CoroutineApiUseCase<Unit, T>
    val setter: CoroutineApiUseCase<T, Unit>
}