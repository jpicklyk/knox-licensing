package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase

class GetElectronicSimEnabledUseCase: SuspendingUseCase<Unit, Boolean>() {
    val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val result = settingsManager.esimEnabled
        return ApiResult.Success(result)
    }

}