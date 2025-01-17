package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.backlight.GetLcdBacklightEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.backlight.SetLcdBacklightEnabledUseCase

@FeatureDefinition(
    title = "Disable LCD Backlight",
    description = "This feature switches ON or OFF the screen's backlight.  " +
    "On OLED screens, this results in the screen being completely on or off.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.INVERTED,
)
class LcdBacklightPolicy() : BooleanPolicy() {
    private val getUseCase = GetLcdBacklightEnabledUseCase()
    private val setUseCase = SetLcdBacklightEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}
