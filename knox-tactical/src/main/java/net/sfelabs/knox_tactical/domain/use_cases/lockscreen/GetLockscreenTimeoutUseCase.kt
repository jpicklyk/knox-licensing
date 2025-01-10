package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase

class GetLockscreenTimeoutUseCase: SuspendingUseCase<Unit, Int>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(systemManager.activityTime)
    }
}