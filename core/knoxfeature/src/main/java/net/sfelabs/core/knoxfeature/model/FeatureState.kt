package net.sfelabs.core.knoxfeature.model

import net.sfelabs.core.domain.api.ApiResult

data class FeatureState<out T>(val enabled: Boolean, val value: T)

fun <T : Any> ApiResult<T>.wrapInFeatureState(): ApiResult<FeatureState<T>> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(FeatureState(true, data))
        is ApiResult.Error -> this
        is ApiResult.NotSupported -> this
    }
}