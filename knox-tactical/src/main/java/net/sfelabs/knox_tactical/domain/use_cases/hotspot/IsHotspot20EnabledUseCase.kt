package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class IsHotspot20EnabledUseCase: CoroutineApiUseCase<Unit, Boolean>() {
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return when(val result = settingsManager.hotspot20State) {
            CustomDeviceManager.ON -> ApiResult.Success(true)
            CustomDeviceManager.OFF -> ApiResult.Success(false)
            else -> ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Unexpected value returned: $result"
                )
            )
        }
    }
}