package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetElectronicSimEnabledUseCase: SuspendingUseCase<SetElectronicSimEnabledUseCase.Params, Unit>() {
    data class Params(val enable: Boolean)
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return invoke(Params(enable))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = settingsManager.setEsimEnabled(params.enable)
        return if(result == CustomDeviceManager.SUCCESS)
            ApiResult.Success(Unit)
        else
            ApiResult.Error(DefaultApiError.UnexpectedError("setEsimEnabled API call failed"))
    }
}