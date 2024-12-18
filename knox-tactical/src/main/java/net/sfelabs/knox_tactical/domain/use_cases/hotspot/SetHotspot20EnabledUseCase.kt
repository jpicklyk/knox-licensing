package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class SetHotspot20EnabledUseCase: CoroutineApiUseCase<Boolean, Unit>() {
    val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        val result = if(params) {
            settingsManager.setHotspot20State(CustomDeviceManager.ON)
        } else {
            settingsManager.setHotspot20State(CustomDeviceManager.OFF)
        }

        return if(result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError("The operation failed for an unknown reason"))
        }
    }
}