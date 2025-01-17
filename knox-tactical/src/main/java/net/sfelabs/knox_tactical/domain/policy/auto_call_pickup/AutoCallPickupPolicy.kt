package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.core.knox.feature.api.FeatureParameters
import net.sfelabs.core.knox.feature.api.StateMapping
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoCallPickupStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoCallPickupStateUseCase

@FeatureDefinition(
    title = "Auto Call Pickup",
    description = "Configure how calls are automatically answered. Options: Disabled, Enabled (with confirmation), or Always Accept (auto-answer without confirmation).",
    category = FeatureCategory.ConfigurableToggle,
    stateMapping = StateMapping.DIRECT
)
class AutoCallPickupPolicy : FeatureContract<AutoCallPickupState> {
    private val getUseCase = GetAutoCallPickupStateUseCase()
    private val setUseCase = SetAutoCallPickupStateUseCase()

    override val defaultValue = AutoCallPickupState(
        isEnabled = false,
        mode = AutoCallPickupMode.Disable
    )

    override suspend fun getState(parameters: FeatureParameters): AutoCallPickupState {
        return when (val result = getUseCase()) {
            is ApiResult.Success -> AutoCallPickupState(
                isEnabled = true,
                mode = result.data
            )
            is ApiResult.NotSupported -> defaultValue.copy(
                isSupported = false
            )
            is ApiResult.Error -> defaultValue.copy(
                error = result.apiError,
                exception = result.exception
            )
        }
    }

    override suspend fun setState(state: AutoCallPickupState): ApiResult<Unit> {
        return setUseCase(state)
    }
}