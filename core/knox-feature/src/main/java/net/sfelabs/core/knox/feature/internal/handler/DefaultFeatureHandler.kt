package net.sfelabs.core.knox.feature.internal.handler

import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.core.knox.feature.internal.model.wrapInFeatureState

class DefaultFeatureHandler<T: Any, P: Any>(
    private val getter: SuspendingUseCase<FeatureParameters, T>,
    private val setter: SuspendingUseCase<P, Unit>,
    private val stateMapping: StateMapping,
    private val parameterWrapper: (T) -> P
) : FeatureHandler<T> {
    override suspend fun getState(parameters: FeatureParameters) =
        getter(parameters).wrapInFeatureState(stateMapping)
    override suspend fun setState(newState: FeatureState<T>) =
        setter(parameterWrapper(newState.value))
}