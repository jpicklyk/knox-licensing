package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.api.domain.model.ApiResult

interface FeatureContract<T : Any> {
    suspend fun getState(parameters: FeatureParameters = FeatureParameters.None): ApiResult<T>
    suspend fun setState(state: T): ApiResult<Unit>
    val defaultValue: T
        get() = throw IllegalStateException(
            "Default value not provided for complex type. Please override this property."
        )
}