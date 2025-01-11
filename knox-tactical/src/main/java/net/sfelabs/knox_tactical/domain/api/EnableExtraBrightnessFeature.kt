package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetExtraBrightnessUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetExtraBrightnessUseCase

@FeatureDefinition(
    title = "Enable Extra Brightness",
    description = "Enables the screen extra brightness device settings",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class EnableExtraBrightnessFeature: FeatureContract<Boolean> {
    private val getUseCase = GetExtraBrightnessUseCase()
    private val setUseCase = SetExtraBrightnessUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> {
        return getUseCase()
    }

    override suspend fun setState(state: Boolean): ApiResult<Unit> {
        return setUseCase(state)
    }
}