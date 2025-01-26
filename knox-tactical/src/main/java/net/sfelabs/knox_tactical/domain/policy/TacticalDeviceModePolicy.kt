package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.BooleanStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeEnabledUseCase

@PolicyDefinition(
    title = "Tactical Device Mode",
    description = "Activates tactical device mode for enhanced mission capabilities",
    category = PolicyCategory.Toggle
)
class TacticalDeviceModePolicy: BooleanStatePolicy() {
    private val getUseCase = GetTacticalDeviceModeEnabledUseCase()
    private val setUseCase = SetTacticalDeviceModeEnabledUseCase()

    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()

    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}