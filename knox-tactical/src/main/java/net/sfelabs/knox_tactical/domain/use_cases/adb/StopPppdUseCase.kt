package net.sfelabs.knox_tactical.domain.use_cases.adb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class StopPppdUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.stopPPPD()
                if(result == CustomDeviceManager.SUCCESS)
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                else
                    net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("The stop PPPD command failed."))
            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}