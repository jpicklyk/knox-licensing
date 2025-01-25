package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.SetRamPlusStateUseCase

@FeatureDefinition(
    title = "Disable RAM Plus",
    description = "Disables the RAM Plus device settings",
    category = FeatureCategory.Toggle
)
class DisableRamPlusPolicy: BooleanStatePolicy() {
    private val getUseCase = GetRamPlusDisabledStateUseCase()
    private val setUseCase = SetRamPlusStateUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}