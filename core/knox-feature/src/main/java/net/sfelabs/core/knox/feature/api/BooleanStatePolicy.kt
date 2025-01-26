package net.sfelabs.core.knox.feature.api

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.ui.model.ConfigurationOption

/**
 * Base class for simple boolean policies that only need to handle enabled/disabled state.
 */
abstract class BooleanStatePolicy(
    private val stateMapping: StateMapping = StateMapping.DIRECT
) : FeatureContract<BooleanPolicyState>, PolicyUiConverter<BooleanPolicyState> {

    override val defaultValue = BooleanPolicyState(isEnabled = false)

    protected abstract suspend fun getEnabled(): ApiResult<Boolean>
    protected abstract suspend fun setEnabled(enabled: Boolean): ApiResult<Unit>

    private fun mapEnabled(enabled: Boolean): Boolean = when (stateMapping) {
        StateMapping.DIRECT -> enabled
        StateMapping.INVERTED -> !enabled
    }

    override suspend fun getState(parameters: PolicyParameters): BooleanPolicyState {
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

    /**
     * Convert UI state to domain state.
     * For boolean policies, this is a direct conversion as the domain state
     * is already in the correct form for the UI.
     */
    override fun fromUiState(uiEnabled: Boolean, options: List<ConfigurationOption>): BooleanPolicyState {
        return BooleanPolicyState(
            isEnabled = uiEnabled  // UI state is already in correct form, do not use mapEnabled
        )
    }

    /**
     * Boolean policies have no configuration options.
     */
    override fun getConfigurationOptions(state: BooleanPolicyState): List<ConfigurationOption> =
        emptyList()
}