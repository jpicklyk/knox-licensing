package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState

class GetEthernetAutoConnectionUseCase: SuspendingUseCase<Unit, AutoConnectionState>() {
    private val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Unit): ApiResult<AutoConnectionState> {
        return ApiResult.Success(AutoConnectionState(settingsManager.ethernetAutoConnectionState))
    }
}