package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
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
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.INVERTED,
)

class DisableImsPolicy: FeatureContract<ImsState> {
    override val defaultValue = ImsState(
        isEnabled = false,
        simSlotId = 0
    )

    private val getUseCase = IsImsEnabledUseCase()
    private val setUseCase = SetImsEnabled()

    override suspend fun getState(parameters: FeatureParameters): ImsState {
        val simSlotId = when (parameters) {
            is ImsParameters -> parameters.simSlotId
            FeatureParameters.None -> defaultValue.simSlotId
            else -> throw IllegalArgumentException("Unsupported parameters type: ${parameters.javaClass.simpleName}")
        }
        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> ImsState(
                isEnabled = result.data,
                simSlotId = simSlotId
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

    override suspend fun setState(state: ImsState): ApiResult<Unit> = setUseCase(state.simSlotId, state.isEnabled)
}
