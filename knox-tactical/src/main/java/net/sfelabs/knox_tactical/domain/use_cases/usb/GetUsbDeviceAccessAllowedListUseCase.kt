package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetUsbDeviceAccessAllowedListUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiCall<String> {
        return coroutineScope {
            try {
                ApiCall.Success(systemManager.usbDeviceAccessAllowedList)
            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}