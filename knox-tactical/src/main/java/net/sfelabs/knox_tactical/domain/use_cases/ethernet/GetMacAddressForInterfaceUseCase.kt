package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import com.samsung.android.knox.custom.SystemManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetMacAddressForInterfaceUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    operator fun invoke(interfaceName: String): net.sfelabs.core.knox.api.domain.ApiResult<String> {
        return try {
            val result = systemManager.getMacAddressForEthernetInterface(interfaceName)
            if (result == null) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "Ethernet interface doesn't exist"
                    )
                )
            } else {
                ApiResult.Success(result)
            }

        } catch (nsm: NoSuchMethodError) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "getMacAddressForEthernetInterface API does not exist on this device"
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    e.message!!
                )
            )
        }
    }
}