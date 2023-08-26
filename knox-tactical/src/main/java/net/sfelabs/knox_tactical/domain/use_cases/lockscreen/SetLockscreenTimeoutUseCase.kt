package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.UiText
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
                    ApiCall.Success(Unit)
                } else {
                    ApiCall.Error(UiText.DynamicString("An invalid timeout $seconds has been specified"))
                }

            } catch (e: SecurityException) {
                ApiCall.Error(uiText =
                UiText.DynamicString(
                    e.message?:"Calling application does not have the required permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}