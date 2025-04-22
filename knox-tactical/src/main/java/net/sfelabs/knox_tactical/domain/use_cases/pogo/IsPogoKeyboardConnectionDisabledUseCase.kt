package net.sfelabs.knox_tactical.domain.use_cases.pogo

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult

class IsPogoKeyboardConnectionDisabledUseCase: SuspendingUseCase<Unit, Boolean>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.isPOGOKeyboardConnectionDisabled)
    }

}