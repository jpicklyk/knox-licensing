package net.sfelabs.knox_tactical.domain.policy.nr_mode

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.data.dto.LteNrModeDto
import net.sfelabs.knox_tactical.domain.model.LteNrMode
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set5gNrModeUseCase

data class NrModeParameters(
    val simSlotId: Int? = null
) : PolicyParameters

@PolicyDefinition(
    title = "5G NR Mode",
    description = "Configure 5G NR (New Radio) mode settings to control SA and NSA capabilities.  " +
            "Turning off the policy will automatically enable both SA and NSA modes.",
    category = PolicyCategory.ConfigurableToggle
)
class NrModePolicy : ConfigurableStatePolicy<NrModeState, LteNrModeDto, NrModeConfiguration>(
    stateMapping = StateMapping.DIRECT
) {
    private val getUseCase = Get5gNrModeUseCase()
    private val setUseCase = Set5gNrModeUseCase()
    override val configuration = NrModeConfiguration(stateMapping = stateMapping)

    override val defaultValue = NrModeState(
        isEnabled = false,
        mode = LteNrMode.EnableBothSaAndNsa
    )

    override suspend fun getState(parameters: PolicyParameters): NrModeState {
        val simSlotId = (parameters as? NrModeParameters)?.simSlotId

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> configuration.fromApiData(result.data)
            is ApiResult.NotSupported -> defaultValue.copy(
                isSupported = false
            )
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: NrModeState): ApiResult<Unit> {
        return setUseCase(configuration.toApiData(state))
    }

}