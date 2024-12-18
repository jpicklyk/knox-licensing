package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.backlight.GetLcdBacklightEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.backlight.SetLcdBacklightEnabledUseCase

@FeatureDefinition(
    title = "Disable LCD Backlight",
    description = "This feature switches ON or OFF the screen's backlight.  " +
    "On OLED screens, this results in the screen being completely on or off.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.INVERTED,
)
class LcdBacklightFeature() : FeatureContract<Boolean> {
    private val getUseCase = GetLcdBacklightEnabledUseCase()
    private val setUseCase = SetLcdBacklightEnabledUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> {
        return getUseCase()
    }

    override suspend fun setState(value: Boolean): ApiResult<Unit> {
        return setUseCase(value)
    }
}
