package net.sfelabs.core.knox.feature.domain.handler

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.domain.model.FeatureState

interface FeatureHandler<T> {
    suspend fun getState(): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}