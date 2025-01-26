package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.IsHotspot20EnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20EnabledUseCase

@PolicyDefinition(
    title = "Disable Hotspot 2.0",
    description = "Disables the automatic hotspot 2.0 connection",
    category = PolicyCategory.Toggle
)
class DisableHotspot20Policy: BooleanStatePolicy(stateMapping = StateMapping.INVERTED) {
    private val getUseCase = IsHotspot20EnabledUseCase()
    private val setUseCase = SetHotspot20EnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()
    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}