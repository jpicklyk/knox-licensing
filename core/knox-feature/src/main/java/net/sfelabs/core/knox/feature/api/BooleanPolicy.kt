package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiResult

abstract class BooleanPolicy : FeatureContract<BooleanPolicyState> {
    override val defaultValue = BooleanPolicyState(isEnabled = false)

    protected abstract suspend fun getEnabled(): ApiResult<Boolean>
    protected abstract suspend fun setEnabled(enabled: Boolean): ApiResult<Unit>

    override suspend fun getState(parameters: FeatureParameters): BooleanPolicyState {
        return when (val result = getEnabled()) {
            is ApiResult.Success -> BooleanPolicyState(
                isEnabled = result.data
            )
            is ApiResult.NotSupported -> defaultValue.copy(
                isSupported = false
            )
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: BooleanPolicyState): ApiResult<Unit> =
        setEnabled(state.isEnabled)
}