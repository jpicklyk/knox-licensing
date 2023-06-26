package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetWlan0MtuUseCase @Inject constructor(
        @TacticalSdk private val systemManager: SystemManager
    ) {
        suspend operator fun invoke(value: Int): UnitApiCall {
            return coroutineScope {
                try {
                    val result = systemManager.setWlanZeroMtu(value)
                    if(result == CustomDeviceManager.SUCCESS)
                        ApiCall.Success(Unit)
                    else
                        ApiCall.Error(UiText.DynamicString("The wlan interface MTU was not set correctly"))
                } catch (e: Exception) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            e.message!!
                        ))
                }
            }
        }
    }

