package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class SetExtraBrightnessUseCase: SuspendingUseCase<SetExtraBrightnessUseCase.Params, Unit>() {
    data class Params(val enable: Boolean)

    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return invoke(Params(enable))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = if(params.enable) {
            settingsManager.setExtraBrightness(CustomDeviceManager.ON)
        } else {
            settingsManager.setExtraBrightness(CustomDeviceManager.OFF)
        }
        return when (result) {
            CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
            CustomDeviceManager.ERROR_NOT_SUPPORTED -> ApiResult.NotSupported
            else -> {
                ApiResult.Error(DefaultApiError.UnexpectedError("Unknown error occurred"))
            }
        }
    }
}