package net.sfelabs.knox_tactical.domain.use_cases.backlight

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase

class GetLcdBacklightEnabledUseCase() : SuspendingUseCase<Unit, Boolean>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.lcdBacklightState)
    }
}