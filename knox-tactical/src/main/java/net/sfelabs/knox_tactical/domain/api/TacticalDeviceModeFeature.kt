package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeEnabledUseCase

@FeatureDefinition(
    title = "Tactical Device Mode",
    description = "Activates tactical device mode for enhanced mission capabilities",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class TacticalDeviceModeFeature: FeatureContract<Boolean> {
    private val getUseCase = GetTacticalDeviceModeEnabledUseCase()
    private val setUseCase = SetTacticalDeviceModeEnabledUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> {
        return getUseCase()
    }

    override suspend fun setState(state: Boolean): ApiResult<Unit> {
        return setUseCase(state)
    }

}