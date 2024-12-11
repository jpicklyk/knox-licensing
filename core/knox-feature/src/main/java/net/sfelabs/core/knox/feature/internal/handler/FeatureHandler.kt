package net.sfelabs.core.knox.feature.internal.handler

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.internal.model.FeatureState

interface FeatureHandler<T : Any> {
    suspend fun getState(): ApiResult<FeatureState<T>>
    suspend fun setState(newState: FeatureState<T>): ApiResult<Unit>
}