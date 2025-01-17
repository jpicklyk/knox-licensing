package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class SetAutoCallPickupStateUseCase: SuspendingUseCase<AutoCallPickupState, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(mode: AutoCallPickupMode): ApiResult<Unit> {
        return invoke(AutoCallPickupState(
            isEnabled = mode != AutoCallPickupMode.Disable,
            mode = mode
        ))
    }

    override suspend fun execute(params: AutoCallPickupState): ApiResult<Unit> {
        return when (systemManager.setAutoCallPickupState(params.mode.value)) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            CustomDeviceManager.ERROR_INVALID_MODE_TYPE -> {
                ApiResult.Error(DefaultApiError.UnexpectedError("Error Invalid Mode Type"))
            }
            else -> {
                ApiResult.Error(DefaultApiError.UnexpectedError("Error not supported"))
            }
        }
    }
}