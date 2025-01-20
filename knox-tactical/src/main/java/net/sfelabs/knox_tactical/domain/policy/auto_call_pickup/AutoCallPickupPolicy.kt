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
        mode = AutoCallPickupMode.EnableAlwaysAccept
    )

    override suspend fun getState(parameters: FeatureParameters): AutoCallPickupState {
        return when (val result = getUseCase()) {
            // We do not want the mode to list as Disable but to default to EnableAlwaysAccept
            is ApiResult.Success -> AutoCallPickupState(
                isEnabled = result.data != AutoCallPickupMode.Disable,
                mode = result.data.takeUnless { it == AutoCallPickupMode.Disable }
                    ?: AutoCallPickupMode.EnableAlwaysAccept
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
        return if(!state.isEnabled)
            setUseCase(AutoCallPickupMode.Disable)
        else
            setUseCase(state.mode)
    }
}