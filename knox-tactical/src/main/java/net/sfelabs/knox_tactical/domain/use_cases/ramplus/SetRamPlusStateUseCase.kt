package net.sfelabs.knox_tactical.domain.use_cases.ramplus

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError

class SetRamPlusStateUseCase: SuspendingUseCase<SetRamPlusStateUseCase.Params, Unit>() {
    data class Params(val disable: Boolean)
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(disable: Boolean): UnitApiCall {
        return invoke(Params(disable))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = systemManager.setRamPlusDisableState(params.disable)

        return if(result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError("The operation failed for an unknown reason"))
        }
    }
}