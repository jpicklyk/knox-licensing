package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupState

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class GetAutoCallPickupStateUseCase: SuspendingUseCase<Unit, AutoCallPickupState>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<AutoCallPickupState> {
        return ApiResult.Success(AutoCallPickupState.invoke(systemManager.autoCallPickupState))
    }
}