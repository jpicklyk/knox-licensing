package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetLockscreenTimeoutUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(seconds: Int): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.setActivityTime(seconds)
                if (result == CustomDeviceManager.SUCCESS) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(DefaultApiError.UnexpectedError("An invalid timeout $seconds has been specified"))
                }

            } catch (e: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}