package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityEnabledUseCase

@FeatureDefinition(
    title = "Enable Auto Sensitivity",
    description = "This feature switches ON or OFF the touch sensitivity functionality in the " +
        "device settings.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class AutoTouchSensitivityFeature() : FeatureContract<Boolean> {
    private val getUseCase = GetAutoTouchSensitivityEnabledUseCase()
    private val setUseCase = SetAutoTouchSensitivityEnabledUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> = getUseCase()

    override suspend fun setState(state: Boolean): ApiResult<Unit> = setUseCase(state)
}
