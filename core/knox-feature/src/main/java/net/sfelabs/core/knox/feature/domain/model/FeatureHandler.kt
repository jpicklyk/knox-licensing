package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.api.domain.ApiResult

interface FeatureHandler<T> {
    suspend fun getState(): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}