package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.CustomDeviceManager.ERROR_NOT_SUPPORTED
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class GetBandLockingStateUseCase: SuspendingUseCase<GetBandLockingStateUseCase.Params, Int>() {
    class Params(val simSlotId: Int? = null)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(): ApiResult<Int> {
        return invoke(Params(null))
    }

    suspend operator fun invoke(simSlotId: Int? = null): ApiResult<Int> {
        return invoke(Params(simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<Int> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.lteBandLocking
            else -> systemManager.getLteBandLockingPerSimSlot(params.simSlotId)
        }
        return when(result) {
            ERROR_NOT_SUPPORTED -> ApiResult.Error(DefaultApiError.UnexpectedError("Error, SIM card required for policy."))
            else -> ApiResult.Success(result)
        }
    }
}

