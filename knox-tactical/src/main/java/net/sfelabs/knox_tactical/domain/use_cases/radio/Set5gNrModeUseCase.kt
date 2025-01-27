package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.data.dto.LteNrModeDto
import net.sfelabs.knox_tactical.domain.model.LteNrMode

class Set5gNrModeUseCase: SuspendingUseCase<LteNrModeDto, Unit>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(state: LteNrMode, simSlotId: Int? = null): UnitApiCall {
        return invoke(LteNrModeDto(mode = state, simSlotId = simSlotId))
    }

    override suspend fun execute(params: LteNrModeDto): ApiResult<Unit> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.set5gNrModeState(params.mode.value)
            else -> systemManager.set5gNrModeStatePerSimSlot(params.mode.value, params.simSlotId)
        }
        return if( result != CustomDeviceManager.SUCCESS ) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Setting 5gNrModeState error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}