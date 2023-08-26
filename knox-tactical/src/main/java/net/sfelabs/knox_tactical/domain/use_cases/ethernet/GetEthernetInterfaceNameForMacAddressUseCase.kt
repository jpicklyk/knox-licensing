package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetEthernetInterfaceNameForMacAddressUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(macAddress: String): ApiCall<String> {
        return try {
            val result = systemManager.getEthernetInterfaceNameForMacAddress(macAddress)
            if(result == null) {
                ApiCall.Error(UiText.DynamicString("MAC Address doesn't exist"))
            } else {
                ApiCall.Success(result)
            }

        } catch (e: Exception) {
            ApiCall.Error(
                UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}