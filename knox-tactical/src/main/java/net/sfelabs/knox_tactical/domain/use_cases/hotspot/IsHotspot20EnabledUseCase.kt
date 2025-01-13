package net.sfelabs.knox_tactical.domain.use_cases.hotspot

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class IsHotspot20EnabledUseCase: SuspendingUseCase<Unit, Boolean>() {
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