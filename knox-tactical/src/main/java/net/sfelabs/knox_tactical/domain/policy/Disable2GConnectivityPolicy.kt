package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.radio.Is2gConnectivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set2gConnectivityEnabled

@FeatureDefinition(
    title = "Disable 2G Connectivity",
    description = "Enable or disable 2G cellular network connectivity.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.INVERTED
)
class Disable2GConnectivityPolicy : BooleanPolicy() {
    private val getUseCase = Is2gConnectivityEnabledUseCase()
    private val setUseCase = Set2gConnectivityEnabled()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}