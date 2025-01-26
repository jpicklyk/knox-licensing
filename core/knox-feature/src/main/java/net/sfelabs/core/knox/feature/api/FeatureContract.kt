package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiResult

interface FeatureContract<T : Any> {
    suspend fun getState(parameters: PolicyParameters = PolicyParameters.None): T
    suspend fun setState(state: T): ApiResult<Unit>
    val defaultValue: T
        get() = throw IllegalStateException(
            "Default value not provided for complex type. Please override this property."
        )
}