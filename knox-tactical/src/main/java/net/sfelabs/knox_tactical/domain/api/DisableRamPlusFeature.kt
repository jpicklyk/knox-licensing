package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.SetRamPlusStateUseCase

@FeatureDefinition(
    title = "Disable RAM Plus",
    description = "Disables the RAM Plus device settings",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class DisableRamPlusFeature: FeatureContract<Boolean> {
    private val getUseCase = GetRamPlusDisabledStateUseCase()
    private val setUseCase = SetRamPlusStateUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> = getUseCase()

    override suspend fun setState(value: Boolean): ApiResult<Unit> = setUseCase(value)
}