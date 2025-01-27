package net.sfelabs.knox_tactical.domain.policy.modem_ims

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.data.dto.ImsDto
import net.sfelabs.knox_tactical.domain.use_cases.ims.IsImsEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ims.SetImsEnabled

data class ImsParameters(
    val simSlotId: Int = 0
) : PolicyParameters


@PolicyDefinition(
    title = "Disable Modem IMS",
    description = "Disables the cellular modem IMS capability",
    category = PolicyCategory.ConfigurableToggle
)
class DisableImsPolicy : ConfigurableStatePolicy<ImsState, ImsDto, ImsConfiguration>(
    stateMapping = StateMapping.INVERTED
) {
    private val getUseCase = IsImsEnabledUseCase()
    private val setUseCase = SetImsEnabled()
    override val configuration = ImsConfiguration(stateMapping = stateMapping)

    override val defaultValue = ImsState(
        isEnabled = false,
        simSlotId = 0
    )

    override suspend fun getState(parameters: PolicyParameters): ImsState {
        val simSlotId = when (parameters) {
            is ImsParameters -> parameters.simSlotId
            else -> defaultValue.simSlotId
        }

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> configuration.fromApiData(result.data)
            is ApiResult.NotSupported -> defaultValue.copy(isSupported = false)
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: ImsState): ApiResult<Unit> {
        return setUseCase(configuration.toApiData(state))
    }

}
