package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbDeviceAccessAllowedListUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(enable: Boolean, allowedList: String?): net.sfelabs.core.ui.ApiCall<Int> {
        return coroutineScope {
            try {
                if(enable) {
                    net.sfelabs.core.ui.ApiCall.Success(systemManager.setUsbDeviceAccessAllowedList(true, allowedList))
                } else {
                    net.sfelabs.core.ui.ApiCall.Success(systemManager.setUsbDeviceAccessAllowedList(false, "OFF"))
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