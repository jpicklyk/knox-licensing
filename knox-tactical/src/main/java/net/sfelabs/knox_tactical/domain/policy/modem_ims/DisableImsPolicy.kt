package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.ImsState
import net.sfelabs.knox_tactical.domain.use_cases.ims.IsImsEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ims.SetImsEnabled

data class ImsParameters(
    val simSlotId: Int = 0
) : FeatureParameters


@FeatureDefinition(
    title = "Disable Modem IMS",
    description = "Disables the cellular modem IMS capability",
    category = FeatureCategory.ConfigurableToggle
)
class DisableImsPolicy : ConfigurableStatePolicy<ImsState, ImsConfiguration>(
    stateMapping = StateMapping.INVERTED
) {
    private val getUseCase = IsImsEnabledUseCase()
    private val setUseCase = SetImsEnabled()

    override val defaultValue = ImsState(
        isEnabled = false,
        simSlotId = 0
    )

    override suspend fun getState(parameters: FeatureParameters): ImsState {
        val simSlotId = when (parameters) {
            is ImsParameters -> parameters.simSlotId
            else -> defaultValue.simSlotId
        }

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> ImsConfiguration(
                enabled = result.data,
                simSlotId = simSlotId,
                stateMapping = stateMapping
            ).toState(defaultValue)
            is ApiResult.NotSupported -> defaultValue.copy(
                isSupported = false
            )
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: ImsState): ApiResult<Unit> {
        val config = toConfiguration(state)
        return setUseCase(config.simSlotId, config.enabled)
    }

    override fun toConfiguration(state: ImsState): ImsConfiguration =
        ImsConfiguration(
            enabled = state.isEnabled,
            simSlotId = state.simSlotId,
            stateMapping = stateMapping
        )
}
