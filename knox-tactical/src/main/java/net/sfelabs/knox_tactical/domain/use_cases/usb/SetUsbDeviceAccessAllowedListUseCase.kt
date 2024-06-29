package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetUsbDeviceAccessAllowedListUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(enable: Boolean, allowedList: String?): ApiResult<Int> {
        return coroutineScope {
            try {
                if(enable) {
                    ApiResult.Success(systemManager.setUsbDeviceAccessAllowedList(true, allowedList))
                } else {
                    ApiResult.Success(systemManager.setUsbDeviceAccessAllowedList(false, "OFF"))
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