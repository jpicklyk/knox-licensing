package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.IsHotspot20EnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20EnabledUseCase

@FeatureDefinition(
    title = "Disable Hotspot 2.0",
    description = "Disables the automatic hotspot 2.0 connection",
    category = FeatureCategory.Toggle
)
class DisableHotspot20Policy: BooleanStatePolicy(stateMapping = StateMapping.INVERTED) {
    private val getUseCase = IsHotspot20EnabledUseCase()
    private val setUseCase = SetHotspot20EnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}