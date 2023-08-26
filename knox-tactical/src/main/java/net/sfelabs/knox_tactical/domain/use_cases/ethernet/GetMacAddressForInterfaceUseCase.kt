package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetMacAddressForInterfaceUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(interfaceName: String): ApiCall<String> {
        return try {
            val result = systemManager.getMacAddressForEthernetInterface(interfaceName)
            if (result == null) {
                ApiCall.Error(UiText.DynamicString("Ethernet interface doesn't exist"))
            } else {
                ApiCall.Success(result)
            }

        } catch (nsm: NoSuchMethodError) {
            ApiCall.Error(
                UiText.DynamicString(
                    "getMacAddressForEthernetInterface API does not exist on this device"
                ))
        } catch (e: Exception) {
            ApiCall.Error(
                UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}