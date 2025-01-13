package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetLockscreenTimeoutUseCase: SuspendingUseCase<SetLockscreenTimeoutUseCase.Params, Unit>() {
    data class Params(val seconds: Int)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(seconds: Int): UnitApiCall {
        return invoke(Params(seconds))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = systemManager.setActivityTime(params.seconds)
        return if (result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError(
                "An invalid timeout ${params.seconds} has been specified"
            ))
        }
    }
}