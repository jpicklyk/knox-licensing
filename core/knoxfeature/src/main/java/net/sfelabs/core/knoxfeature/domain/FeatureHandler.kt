package net.sfelabs.core.knoxfeature.domain

import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.model.FeatureState

interface FeatureHandler<T> {
    suspend fun getState(): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}