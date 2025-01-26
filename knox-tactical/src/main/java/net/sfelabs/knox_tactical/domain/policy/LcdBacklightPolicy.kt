package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.backlight.GetLcdBacklightEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.backlight.SetLcdBacklightEnabledUseCase

@PolicyDefinition(
    title = "Disable LCD Backlight",
    description = "This feature switches ON or OFF the screen's backlight.  " +
    "On OLED screens, this results in the screen being completely on or off.",
    category = PolicyCategory.Toggle
)
class LcdBacklightPolicy() : BooleanStatePolicy(stateMapping = StateMapping.INVERTED) {
    private val getUseCase = GetLcdBacklightEnabledUseCase()
    private val setUseCase = SetLcdBacklightEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}
