package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbDeviceAccessAllowedListUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(enable: Boolean, allowedList: String?): ApiCall<Int> {
        return coroutineScope {
            try {
                if(enable) {
                    ApiCall.Success(systemManager.setUsbDeviceAccessAllowedList(true, allowedList))
                } else {
                    ApiCall.Success(systemManager.setUsbDeviceAccessAllowedList(false, "OFF"))
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