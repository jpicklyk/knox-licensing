package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class Disable5gBandLockingUseCase: SuspendingUseCase<Disable5gBandLockingUseCase.Params, Unit>() {

    class Params(val simSlotId: Int? = null)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(simSlotId: Int? = null): UnitApiCall {
        return invoke(Params(simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }

        val result = when (params.simSlotId) {
            null -> systemManager.disable5GBandLocking()
            else -> systemManager.disable5GBandLockingPerSimSlot(params.simSlotId)
        }
        return if( result != CustomDeviceManager.SUCCESS ) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Disable5gBandLocking error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}