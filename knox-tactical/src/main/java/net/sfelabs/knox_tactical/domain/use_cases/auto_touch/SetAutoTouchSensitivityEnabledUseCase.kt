package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.toOnOrOff

class SetAutoTouchSensitivityEnabledUseCase () : CoroutineApiUseCase<Boolean, Unit>() {
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