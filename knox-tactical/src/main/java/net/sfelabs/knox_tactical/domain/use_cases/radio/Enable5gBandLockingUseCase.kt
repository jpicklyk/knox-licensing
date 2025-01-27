package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.data.dto.BandLockingDto

class Enable5gBandLockingUseCase: SuspendingUseCase<BandLockingDto, Unit>() {


    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(band: Int, simSlotId: Int? = null): UnitApiCall {
        return invoke(BandLockingDto(band = band, simSlotId = simSlotId))
    }

    override suspend fun execute(params: BandLockingDto): ApiResult<Unit> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }

        val result = when (params.simSlotId) {
            null -> systemManager.enable5GBandLocking(params.band)
            else -> systemManager.enable5GBandLockingPerSimSlot(params.band, params.simSlotId)
        }
        return if( result != CustomDeviceManager.SUCCESS ) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Enable5gBandLocking error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}