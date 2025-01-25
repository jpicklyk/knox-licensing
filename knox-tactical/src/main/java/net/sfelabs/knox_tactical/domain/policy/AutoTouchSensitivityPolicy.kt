package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityEnabledUseCase

@FeatureDefinition(
    title = "Enable Auto Sensitivity",
    description = "This feature switches ON or OFF the touch sensitivity functionality in the " +
        "device settings.",
    category = FeatureCategory.Toggle
)
class AutoTouchSensitivityPolicy() : BooleanStatePolicy() {
    private val getUseCase = GetAutoTouchSensitivityEnabledUseCase()
    private val setUseCase = SetAutoTouchSensitivityEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}
