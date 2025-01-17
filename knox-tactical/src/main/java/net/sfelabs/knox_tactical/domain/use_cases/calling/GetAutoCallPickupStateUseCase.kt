package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode

/**
 * This Knox API isn't TE specific but the flag ENABLED_ALWAYS_ACCEPT is.
 */
class GetAutoCallPickupStateUseCase: SuspendingUseCase<Unit, AutoCallPickupMode>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<AutoCallPickupMode> {
        return ApiResult.Success(AutoCallPickupMode.invoke(systemManager.autoCallPickupState))
    }
}