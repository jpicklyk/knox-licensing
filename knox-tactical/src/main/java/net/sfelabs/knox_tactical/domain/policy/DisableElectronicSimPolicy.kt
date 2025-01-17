package net.sfelabs.knox_tactical.domain.policy

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.BooleanPolicy
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.sim.GetElectronicSimEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.sim.SetElectronicSimEnabledUseCase

@FeatureDefinition(
    title = "Disable Electronic SIM Capability.",
    description = "Disables the use of electron SIM in the device settings.",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.INVERTED,
)
class DisableElectronicSimPolicy: BooleanPolicy() {
    private val getUseCase = GetElectronicSimEnabledUseCase()
    private val setUseCase = SetElectronicSimEnabledUseCase()
    override suspend fun getEnabled(): ApiResult<Boolean> = getUseCase()

    override suspend fun setEnabled(enabled: Boolean): ApiResult<Unit> = setUseCase(enabled)
}