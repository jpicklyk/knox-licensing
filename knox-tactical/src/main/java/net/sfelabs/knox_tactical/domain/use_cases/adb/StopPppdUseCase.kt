package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class StopPppdUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.stopPPPD()
                if(result == CustomDeviceManager.SUCCESS)
                    ApiResult.Success(Unit)
                else
                    ApiResult.Error(DefaultApiError.UnexpectedError("The stop PPPD command failed."))
            } catch (e: Exception) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message!!
                    )
                )
            }
        }
    }
}