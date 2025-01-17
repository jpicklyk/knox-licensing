package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetExtraBrightnessUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetExtraBrightnessUseCase

@FeatureDefinition(
    title = "Enable Extra Brightness",
    description = "Enables the screen extra brightness device settings",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class EnableExtraBrightnessPolicy: BooleanPolicy() {
    private val getUseCase = GetExtraBrightnessUseCase()
    private val setUseCase = SetExtraBrightnessUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}