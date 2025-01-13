package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetEthernetAutoConnectionAltUseCase
    : SuspendingUseCase<SetEthernetAutoConnectionAltUseCase.Params, Unit>() {

    class Params(
        val enable: Boolean
    )
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return invoke(Params(enable))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = settingsManager.setEthernetAutoConnectionState(
            if(params.enable) 1 else 0
        )
        return if(result == CustomDeviceManager.SUCCESS) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(DefaultApiError.UnexpectedError("Device does not support this method"))
        }
    }

}