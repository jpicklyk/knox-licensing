package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiResult

/**
 * Base class for simple boolean policies that only need to handle enabled/disabled state.
 */
abstract class ConfigurableBooleanPolicy(
    private val stateMapping: StateMapping = StateMapping.DIRECT
) : FeatureContract<BooleanPolicyState> {

    override val defaultValue = BooleanPolicyState(isEnabled = false)

    protected abstract suspend fun getEnabled(): ApiResult<Boolean>
    protected abstract suspend fun setEnabled(enabled: Boolean): ApiResult<Unit>

    private fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
        StateMapping.DIRECT -> enabled
        StateMapping.INVERTED -> !enabled
    }

    override suspend fun getState(parameters: FeatureParameters): BooleanPolicyState {
        return when (val result = getEnabled()) {
            is ApiResult.Success -> defaultValue.copy(
                isEnabled = mapEnabled(result.data)
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
        setEnabled(mapEnabled(state.isEnabled))
}