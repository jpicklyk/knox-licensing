package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetUsbAccessBySerialUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiResult<String> {
        return coroutineScope {
            try {
                ApiResult.Success(systemManager.usbDeviceAccessAllowedListSerialNumber)
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