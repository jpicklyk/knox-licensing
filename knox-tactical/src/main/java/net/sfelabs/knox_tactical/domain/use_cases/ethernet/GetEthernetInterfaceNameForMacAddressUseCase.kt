package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetEthernetInterfaceNameForMacAddressUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(macAddress: String): ApiResult<String> {
        return try {
            val result = systemManager.getEthernetInterfaceNameForMacAddress(macAddress)
            if(result == null) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "MAC Address doesn't exist"
                    )
                )
            } else {
                ApiResult.Success(result)
            }

        } catch (e: Exception) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    e.message!!
                )
            )
        }
    }
}