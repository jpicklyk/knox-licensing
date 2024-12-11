package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.knox.api.domain.ApiResult

interface FeatureContract<T : Any> {
    /**
     * Gets the current state of the feature.
     *
     * @return An [ApiResult] representing the current state.
     */
    suspend fun getState(): ApiResult<T>

    /**
     * Sets the state of the feature.
     *
     * @param value The value to set.
     * @return An [ApiResult] representing the result of the operation.
     */
    suspend fun setState(value: T): ApiResult<Unit>
}