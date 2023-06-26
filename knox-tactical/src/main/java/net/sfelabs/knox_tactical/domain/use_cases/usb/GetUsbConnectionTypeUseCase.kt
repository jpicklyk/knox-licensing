package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import javax.inject.Inject

class GetUsbConnectionTypeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<UsbConnectionType> {
        return coroutineScope {
            try {
                val result = systemManager.usbConnectionType
                net.sfelabs.core.ui.ApiCall.Success(UsbConnectionType(result))
            } catch (e: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}