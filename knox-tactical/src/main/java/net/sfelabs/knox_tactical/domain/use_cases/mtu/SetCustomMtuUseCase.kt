package net.sfelabs.knox_tactical.domain.use_cases.mtu

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetCustomMtuUseCase @Inject constructor(
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

