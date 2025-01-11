package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError

class GetExtraBrightnessUseCase: SuspendingUseCase<Unit, Boolean>() {
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return when (settingsManager.extraBrightness) {
            CustomDeviceManager.ON -> ApiResult.Success(true)
            CustomDeviceManager.OFF -> ApiResult.Success(false)
            else ->
                ApiResult.Error(DefaultApiError.UnexpectedError("Unknown error occurred"))
        }
    }
}