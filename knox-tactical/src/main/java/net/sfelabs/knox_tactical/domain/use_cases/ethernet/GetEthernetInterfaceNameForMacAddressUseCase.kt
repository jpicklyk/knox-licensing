package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetEthernetInterfaceNameForMacAddressUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(macAddress: String): net.sfelabs.core.ui.ApiCall<String> {
        return try {
            val result = systemManager.getEthernetInterfaceNameForMacAddress(macAddress)
            if(result == null) {
                net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("MAC Address doesn't exist"))
            } else {
                net.sfelabs.core.ui.ApiCall.Success(result)
            }

        } catch (e: Exception) {
            net.sfelabs.core.ui.ApiCall.Error(
                net.sfelabs.core.ui.UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}