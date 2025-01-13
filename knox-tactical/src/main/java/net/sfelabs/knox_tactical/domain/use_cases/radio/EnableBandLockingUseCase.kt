package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class EnableBandLockingUseCase: SuspendingUseCase<EnableBandLockingUseCase.Params, Unit>() {

    class Params(
        val band: Int,
        val simSlotId: Int?
    )

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(band: Int, simSlotId: Int? = null): UnitApiCall {
        return invoke(Params(band, simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.enableLteBandLocking(params.band)
            else -> systemManager.enableLteBandLockingPerSimSlot(params.band, params.simSlotId)
        }
        return if (result != CustomDeviceManager.SUCCESS) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "EnableLteBandLocking error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}