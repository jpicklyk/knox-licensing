package net.sfelabs.knox_tactical.domain.api

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.knox_tactical.domain.use_cases.sim.GetElectronicSimEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.sim.SetElectronicSimEnabledUseCase

@FeatureDefinition(
    title = "Disable Electronic SIM Capability.",
    description = "Disables the use of electron SIM in the device settings.",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.INVERTED,
)
class DisableElectronicSimFeature: FeatureContract<Boolean> {
    private val getUseCase = GetElectronicSimEnabledUseCase()
    private val setUseCase = SetElectronicSimEnabledUseCase()

    override suspend fun getState(parameters: FeatureParameters): ApiResult<Boolean> = getUseCase()

    override suspend fun setState(state: Boolean): ApiResult<Unit> {
        return setUseCase(state)
    }
}