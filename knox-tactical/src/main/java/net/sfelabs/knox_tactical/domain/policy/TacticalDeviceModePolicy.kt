package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeEnabledUseCase

@FeatureDefinition(
    title = "Tactical Device Mode",
    description = "Activates tactical device mode for enhanced mission capabilities",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class TacticalDeviceModePolicy: BooleanPolicy() {
    private val getUseCase = GetTacticalDeviceModeEnabledUseCase()
    private val setUseCase = SetTacticalDeviceModeEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()

    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}