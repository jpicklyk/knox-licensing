package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.IsHotspot20EnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20EnabledUseCase

@FeatureDefinition(
    title = "Disable Hotspot 2.0",
    description = "Disables the automatic hotspot 2.0 connection",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.INVERTED,
)
class DisableHotspot20Feature: FeatureContract<Boolean> {
    private val getUseCase = IsHotspot20EnabledUseCase()
    private val setUseCase = SetHotspot20EnabledUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> = getUseCase()

    override suspend fun setState(state: Boolean): ApiResult<Unit> {
        return setUseCase(state)
    }
}