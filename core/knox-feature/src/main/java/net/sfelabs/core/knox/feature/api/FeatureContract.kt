package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.api.domain.ApiResult

interface FeatureContract<T : Any> {
    suspend fun getState(parameters: FeatureParameters = FeatureParameters.None): ApiResult<T>
    suspend fun setState(value: T): ApiResult<Unit>
    val defaultValue: T
        get() = throw IllegalStateException(
            "Default value not provided for complex type. Please override this property."
        )
}