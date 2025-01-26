package net.sfelabs.knox_tactical.domain.policy.hdm

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmPolicyUseCase

@PolicyDefinition(
    title = "Enable HDM Policy",
    description = "Control hardware components through a unified policy. Individual components can " +
            "be disabled for enhanced security.",
    category = PolicyCategory.ConfigurableToggle
)
class EnableHdmPolicy : ConfigurableStatePolicy<HdmState, Int, HdmConfiguration>() {
    private val getUseCase = GetHdmPolicyUseCase()
    private val setUseCase = SetHdmPolicyUseCase()
    override val configuration = HdmConfiguration()

    override val defaultValue = HdmState(
        isEnabled = false,
        policyMask = 0
    )

    override suspend fun getState(parameters: PolicyParameters): HdmState {
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