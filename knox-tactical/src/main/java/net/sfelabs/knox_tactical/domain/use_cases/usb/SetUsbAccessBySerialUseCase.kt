package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
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
                            DefaultApiError.UnexpectedError(
                                "Error invalid value"
                            )
                        )
                    }
                    else -> {
                        ApiResult.Error(
                            DefaultApiError.UnexpectedError(
                                "Error, the arguments were not executed"
                            )
                        )
                    }
                }

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
