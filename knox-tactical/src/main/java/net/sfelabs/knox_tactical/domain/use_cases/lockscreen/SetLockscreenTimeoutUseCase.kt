package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetLockscreenTimeoutUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(seconds: Int): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.setActivityTime(seconds)
                if (result == CustomDeviceManager.SUCCESS) {
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                } else {
                    net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("An invalid timeout $seconds has been specified"))
                }

            } catch (e: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(uiText =
                net.sfelabs.core.ui.UiText.DynamicString(
                    e.message?:"Calling application does not have the required permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}