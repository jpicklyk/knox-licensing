package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.radio.Is2gConnectivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set2gConnectivityEnabled

@PolicyDefinition(
    title = "Disable 2G Connectivity",
    description = "Enable or disable 2G cellular network connectivity.",
    category = PolicyCategory.Toggle
)
class Disable2GConnectivityPolicy : BooleanStatePolicy(stateMapping = StateMapping.INVERTED) {
    private val getUseCase = Is2gConnectivityEnabledUseCase()
    private val setUseCase = Set2gConnectivityEnabled()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}