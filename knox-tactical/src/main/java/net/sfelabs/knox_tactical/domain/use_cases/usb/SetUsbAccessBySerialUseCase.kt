package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbAccessBySerialUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(enable: Boolean, serials: List<String>): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val result =
                    if(enable) {
                        val list = serials.joinToString(separator = ":")
                        systemManager.setUsbDeviceAccessAllowedListSerialNumber(
                            true,
                            list
                        )
                    } else {
                        systemManager.setUsbDeviceAccessAllowedListSerialNumber(
                            false,
                            "OFF"
                        )
                    }

                when (result) {
                    CustomDeviceManager.SUCCESS -> {
                        net.sfelabs.core.ui.ApiCall.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_INVALID_VALUE -> {
                        net.sfelabs.core.ui.ApiCall.Error(
                            net.sfelabs.core.ui.UiText.DynamicString(
                                "Error invalid value"
                            ))
                    }
                    else -> {
                        net.sfelabs.core.ui.ApiCall.Error(
                            net.sfelabs.core.ui.UiText.DynamicString(
                                "Error, the arguments were not executed"
                            ))
                    }
                }

            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
