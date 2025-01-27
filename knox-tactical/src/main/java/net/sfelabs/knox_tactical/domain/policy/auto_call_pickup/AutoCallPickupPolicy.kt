package net.sfelabs.knox_tactical.domain.policy.auto_call_pickup

import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.feature.annotation.PolicyDefinition
import net.sfelabs.core.knox.feature.api.ConfigurableStatePolicy
import net.sfelabs.core.knox.feature.api.PolicyCategory
import net.sfelabs.core.knox.feature.api.PolicyParameters
import net.sfelabs.knox_tactical.data.dto.AutoCallPickupDto
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.use_cases.calling.GetAutoCallPickupStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.calling.SetAutoCallPickupStateUseCase

@PolicyDefinition(
    title = "Auto Call Pickup",
    description = "Configure how calls are automatically answered. Options: Disabled, Enabled (with confirmation), or Always Accept (auto-answer without confirmation).",
    category = PolicyCategory.ConfigurableToggle
)
class AutoCallPickupPolicy :
    ConfigurableStatePolicy<AutoCallPickupState, AutoCallPickupDto, AutoCallPickupConfiguration>() {
    private val getUseCase = GetAutoCallPickupStateUseCase()
    private val setUseCase = SetAutoCallPickupStateUseCase()
    override val configuration = AutoCallPickupConfiguration()

    override val defaultValue = AutoCallPickupState(
        isEnabled = false,
        mode = AutoCallPickupMode.EnableAlwaysAccept
    )

    override suspend fun getState(parameters: PolicyParameters): AutoCallPickupState {
        return when (val result = getUseCase()) {
            is ApiResult.Success -> configuration.fromApiData(result.data)
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
        return setUseCase(configuration.toApiData(state))
    }

}