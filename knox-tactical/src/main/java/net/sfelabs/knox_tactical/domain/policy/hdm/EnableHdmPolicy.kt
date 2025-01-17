package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmPolicyUseCase

@FeatureDefinition(
    title = "Hardware Disable Mode",
    description = "Control hardware components through a unified policy. Individual components can be disabled for enhanced security.",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.DIRECT
)
class EnableHdmPolicy : FeatureContract<HdmState> {
    private val getUseCase = GetHdmPolicyUseCase()
    private val setUseCase = SetHdmPolicyUseCase()

    override val defaultValue = HdmState(
        isEnabled = false,
        policyMask = 0
    )

    override suspend fun getState(parameters: FeatureParameters): HdmState {
        return when (val result = getUseCase()) {
            is ApiResult.Success -> HdmState(
                isEnabled = result.data != 0,
                policyMask = result.data
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

    override suspend fun setState(state: HdmState): ApiResult<Unit> =
        setUseCase(state.policyMask, false)
}