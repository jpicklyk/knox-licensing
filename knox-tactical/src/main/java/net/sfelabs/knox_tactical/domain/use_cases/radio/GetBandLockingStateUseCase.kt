package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.CustomDeviceManager.ERROR_NOT_SUPPORTED
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.data.dto.BandLockingDto

class GetBandLockingStateUseCase: SuspendingUseCase<GetBandLockingStateUseCase.Params, BandLockingDto>() {
    class Params(val simSlotId: Int? = null)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(): ApiResult<BandLockingDto> {
        return invoke(Params(null))
    }

    suspend operator fun invoke(simSlotId: Int? = null): ApiResult<BandLockingDto> {
        return invoke(Params(simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<BandLockingDto> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val band = when (params.simSlotId) {
            null -> systemManager.lteBandLocking
            else -> systemManager.getLteBandLockingPerSimSlot(params.simSlotId)
        }
        return when(band) {
            ERROR_NOT_SUPPORTED -> ApiResult.Error(DefaultApiError.UnexpectedError("Error, SIM card required for policy."))
            else -> ApiResult.Success(BandLockingDto(band = band, simSlotId = params.simSlotId))
        }
    }
}

