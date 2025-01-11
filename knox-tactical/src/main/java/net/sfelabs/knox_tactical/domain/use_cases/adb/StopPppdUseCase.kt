package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError

class StopPppdUseCase : SuspendingUseCase<Unit, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Unit> {
        return when(val result = systemManager.stopPPPD()) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            else -> {
                ApiResult.Error(DefaultApiError.UnexpectedError("The stop PPPD command failed: $result"))
            }
        }
    }
}