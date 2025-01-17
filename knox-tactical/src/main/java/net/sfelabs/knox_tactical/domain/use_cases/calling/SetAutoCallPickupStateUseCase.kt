package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class SetAutoCallPickupStateUseCase: SuspendingUseCase<AutoCallPickupMode, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager


    override suspend fun execute(params: AutoCallPickupMode): ApiResult<Unit> {
        return when (systemManager.setAutoCallPickupState(params.value)) {
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