package net.sfelabs.knox_tactical.domain.policy.nr_mode

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.ConfigurablePolicy
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.LteNrMode
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set5gNrModeUseCase

data class NrModeParameters(
    val simSlotId: Int? = null
) : FeatureParameters

@FeatureDefinition(
    title = "5G NR Mode",
    description = "Configure 5G NR (New Radio) mode settings to control SA and NSA capabilities.  " +
            "Turning off the policy will automatically enable both SA and NSA modes.",
    category = FeatureCategory.ConfigurableToggle
)
class NrModePolicy : ConfigurableStatePolicy<NrModeState, NrModeConfiguration>(
    stateMapping = StateMapping.DIRECT
) {
    private val getUseCase = Get5gNrModeUseCase()
    private val setUseCase = Set5gNrModeUseCase()

    override val defaultValue = NrModeState(
        isEnabled = false,
        mode = LteNrMode.EnableBothSaAndNsa
    )

    override suspend fun getState(parameters: FeatureParameters): NrModeState {
        val simSlotId = (parameters as? NrModeParameters)?.simSlotId

        return when (val result = getUseCase(simSlotId)) {
            is ApiResult.Success -> NrModeConfiguration(
                mode = result.data,
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

    override suspend fun setState(state: NrModeState): ApiResult<Unit> {
        val config = toConfiguration(state)
        return setUseCase(config.mode, config.simSlotId)
    }

    override fun toConfiguration(state: NrModeState): NrModeConfiguration =
        NrModeConfiguration(
            mode = if (!state.isEnabled) LteNrMode.EnableBothSaAndNsa else state.mode,
            simSlotId = state.simSlotId,
            stateMapping = stateMapping
        )

}