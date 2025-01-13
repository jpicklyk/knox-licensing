package net.sfelabs.knox_tactical.domain.use_cases.backlight

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetLcdBacklightEnabledUseCase : SuspendingUseCase<Boolean, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when(val result =systemManager.setLcdBacklightState(params)) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            else -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "An error occurred calling the setLcdBacklightState API: $result"
                    )
                )
            }
        }
    }
}