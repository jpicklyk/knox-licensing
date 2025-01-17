package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.SetRamPlusStateUseCase

@FeatureDefinition(
    title = "Disable RAM Plus",
    description = "Disables the RAM Plus device settings",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class DisableRamPlusPolicy: BooleanPolicy() {
    private val getUseCase = GetRamPlusDisabledStateUseCase()
    private val setUseCase = SetRamPlusStateUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}