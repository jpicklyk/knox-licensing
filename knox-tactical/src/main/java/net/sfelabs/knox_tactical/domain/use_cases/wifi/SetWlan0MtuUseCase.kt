package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError

class SetWlan0MtuUseCase: SuspendingUseCase<SetWlan0MtuUseCase.Params, Unit>() {
    data class Params(val mtu: Int)
    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(value: Int): UnitApiCall {
        return invoke(Params(value))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = systemManager.setWlanZeroMtu(params.mtu)
        return if(result == CustomDeviceManager.SUCCESS)
            ApiResult.Success(Unit)
        else
            ApiResult.Error(DefaultApiError.UnexpectedError("The wlan interface MTU was not set correctly"))
    }
}

