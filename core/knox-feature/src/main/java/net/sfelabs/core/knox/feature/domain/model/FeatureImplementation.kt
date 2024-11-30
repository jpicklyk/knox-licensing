package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

interface FeatureImplementation<T : Any> {
    val getter: CoroutineApiUseCase<*, T>
    val setter: CoroutineApiUseCase<T, *>
}