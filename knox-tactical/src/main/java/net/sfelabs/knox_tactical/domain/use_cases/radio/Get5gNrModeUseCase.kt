package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.data.dto.LteNrModeDto
import net.sfelabs.knox_tactical.domain.model.LteNrMode

class Get5gNrModeUseCase: SuspendingUseCase<Get5gNrModeUseCase.Params, LteNrModeDto>() {
    class Params(val simSlotId: Int? = null)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(): ApiResult<LteNrModeDto> {
        return invoke(Params(null))
    }

    suspend operator fun invoke(simSlotId: Int? = null): ApiResult<LteNrModeDto> {
        return invoke(Params(simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<LteNrModeDto> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.get5gNrModeState()
            else -> {
                println("Calling get5gNrModeStatePerSimSlot")
                systemManager.get5gNrModeStatePerSimSlot(params.simSlotId)
            }
        }
        return if( result == CustomDeviceManager.ERROR_FAIL ) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Getting 5gNrModeState error: Unknown reason"
                )
            )
        } else {
            ApiResult.Success(LteNrModeDto(params.simSlotId, LteNrMode(result)))
        }
    }
}