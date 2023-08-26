package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import javax.inject.Inject

class SetUsbConnectionTypeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(type: UsbConnectionType): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.setUsbConnectionType(type.value)

                if(result == CustomDeviceManager.SUCCESS) {
                    ApiCall.Success(Unit)
                } else {
                    ApiCall.Error(UiText.DynamicString("The specified connection type is invalid"))
                }
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            } catch (ex: Exception) {
                ApiCall.NotSupported
            }
        }
    }
}