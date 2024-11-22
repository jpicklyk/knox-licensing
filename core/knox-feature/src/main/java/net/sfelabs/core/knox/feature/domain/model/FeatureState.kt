package net.sfelabs.core.knox.feature.domain.model

import net.sfelabs.core.knox.api.domain.ApiResult

data class FeatureState<out T>(val enabled: Boolean, val value: T)

fun <T : Any> ApiResult<T>.wrapInFeatureState(): ApiResult<FeatureState<T>> {
    return when (this) {
        //TODO: This can't always be true, there needs to be a definition somewhere of what
        //defines when a feature is enabled or not.
        is ApiResult.Success -> ApiResult.Success(FeatureState(true, data))
        is ApiResult.Error -> this
        is ApiResult.NotSupported -> this
    }
}