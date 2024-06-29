package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetEthernetInterfaceNameForMacAddressUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(macAddress: String): ApiResult<String> {
        return try {
            val result = systemManager.getEthernetInterfaceNameForMacAddress(macAddress)
            if(result == null) {
                ApiResult.Error(UiText.DynamicString("MAC Address doesn't exist"))
            } else {
                ApiResult.Success(result)
            }

        } catch (e: Exception) {
            ApiResult.Error(
                UiText.DynamicString(
                    e.message!!
                ))
        }
    }
}