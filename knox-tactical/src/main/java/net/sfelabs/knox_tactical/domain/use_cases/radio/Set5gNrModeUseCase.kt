package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.LteNrModeState

class Set5gNrModeUseCase: SuspendingUseCase<Set5gNrModeUseCase.Params, Unit>() {
    class Params(val state: LteNrModeState, val simSlotId: Int? = null)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(state: LteNrModeState, simSlotId: Int? = null): UnitApiCall {
        return invoke(Params(state, simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.set5gNrModeState(params.state.value)
            else -> systemManager.set5gNrModeStatePerSimSlot(params.state.value, params.simSlotId)
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