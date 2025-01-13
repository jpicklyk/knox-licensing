package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.feature.internal.model.FeatureState

class Get5gBandLockingUseCase: SuspendingUseCase<Get5gBandLockingUseCase.Params, FeatureState<Int>>() {

    class Params(val simSlotId: Int? = null)
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(): ApiResult<FeatureState<Int>> {
        return invoke(Params(null))
    }

    suspend operator fun invoke(simSlotId: Int? = null): ApiResult<FeatureState<Int>> {
        return invoke(Params(simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<FeatureState<Int>> {
        params.simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        val result = when (params.simSlotId) {
            null -> systemManager.get5GBandLocking()
            else -> systemManager.get5GBandLockingPerSimSlot(params.simSlotId)
        }
        return when(result) {
            CustomDeviceManager.BANDLOCK_NONE -> ApiResult.Success(FeatureState(false, result))
            else -> ApiResult.Success(FeatureState(true, result))
        }
    }
}