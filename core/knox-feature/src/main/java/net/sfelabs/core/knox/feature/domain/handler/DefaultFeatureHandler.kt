package net.sfelabs.core.knox.feature.domain.handler

import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.feature.domain.model.FeatureState
import net.sfelabs.core.knox.feature.domain.model.wrapInFeatureState

class DefaultFeatureHandler<T: Any>(
    private val getter: CoroutineApiUseCase<Unit, T>,
    private val setter: CoroutineApiUseCase<T, Unit>
) : FeatureHandler<T> {
    override suspend fun getState() = getter(Unit).wrapInFeatureState()
    override suspend fun setState(newState: FeatureState<T>) = setter(newState.value)
}