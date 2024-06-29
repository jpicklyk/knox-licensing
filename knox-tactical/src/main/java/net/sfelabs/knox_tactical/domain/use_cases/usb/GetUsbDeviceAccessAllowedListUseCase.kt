package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetUsbDeviceAccessAllowedListUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiResult<String> {
        return coroutineScope {
            try {
                ApiResult.Success(systemManager.usbDeviceAccessAllowedList)
            } catch (e: Exception) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}