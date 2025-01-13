package net.sfelabs.core.knox.feature.internal.handler

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.model.FeatureState

interface FeatureHandler<T : Any> {
    suspend fun getState(parameters: FeatureParameters = FeatureParameters.None): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}