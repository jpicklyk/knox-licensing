package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
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
                        ApiResult.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_INVALID_VALUE -> {
                        ApiResult.Error(
                            UiText.DynamicString(
                                "Error invalid value"
                            ))
                    }
                    else -> {
                        ApiResult.Error(
                            UiText.DynamicString(
                                "Error, the arguments were not executed"
                            ))
                    }
                }

            } catch (e: Exception) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
