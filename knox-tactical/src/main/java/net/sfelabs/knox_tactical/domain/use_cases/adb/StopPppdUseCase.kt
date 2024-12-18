package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class StopPppdUseCase : CoroutineApiUseCase<Unit, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Unit> {
        return when(val result = systemManager.stopPPPD()) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            else -> {
                ApiResult.Error(DefaultApiError.UnexpectedError("The stop PPPD command failed: $result"))
            }
        }
    }
}