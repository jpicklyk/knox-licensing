package net.sfelabs.knox_tactical.domain.use_cases.pogo

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class DisablePogoKeyboardConnectionUseCase : SuspendingUseCase<Boolean, Unit>() {
    private val systemManager : SystemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        val result = systemManager.disablePOGOKeyboardConnection(params)

        return if(result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(
                DefaultApiError.UnexpectedError("The operation failed for an unknown reason")
            )
        }

    }
}