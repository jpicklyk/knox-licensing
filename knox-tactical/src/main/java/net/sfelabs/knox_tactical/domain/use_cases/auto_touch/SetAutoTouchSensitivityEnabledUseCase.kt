package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.toOnOrOff

class SetAutoTouchSensitivityEnabledUseCase () : SuspendingUseCase<Boolean, Unit>() {
    val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when (val result =settingsManager.setAutoAdjustTouchSensitivity(params.toOnOrOff())) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            CustomDeviceManager.ERROR_FAIL -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "An unknown error occurred attempting to set auto touch " +
                                "sensitivity state: $result"
                    )
                )
            }
            else -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "This device does not support the setAutoAdjustTouchSensitivity" +
                                " API"
                    )
                )
            }
        }
    }
}