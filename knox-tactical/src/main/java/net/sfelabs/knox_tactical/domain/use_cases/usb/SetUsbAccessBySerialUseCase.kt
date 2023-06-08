package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.UsbDeviceAccessState
import javax.inject.Inject

class SetUsbAccessBySerialUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(enable: Boolean, serials: List<String>): UnitApiCall {
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
                        ApiCall.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_INVALID_VALUE -> {
                        ApiCall.Error(
                            UiText.DynamicString(
                                "Error invalid value"
                            ))
                    }
                    else -> {
                        ApiCall.Error(
                            UiText.DynamicString(
                                "Error, the arguments were not executed"
                            ))
                    }
                }

            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
