package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import android.net.ConnectivityManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureDhcpEthernetInterface
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureStaticEthernetInterface
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class ConfigureEthernetInterfaceAltUseCase @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(
        interfaceName: String,
        useDhcp: Boolean,
        ipAddress: String,
        netmask: String,
        dnsAddressList: List<String> = emptyList(),
        defaultRouter: String? = null
    ):UnitApiCall {
        return coroutineScope {
            try {
                val isSuccessful = when (useDhcp) {
                    true -> systemManager.configureDhcpEthernetInterface(interfaceName)
                    false -> systemManager.configureStaticEthernetInterface(
                        interfaceName,
                        ipAddress,
                        dnsAddressList,
                        defaultRouter,
                        netmask
                    )
                }
                if (isSuccessful) {
                    ApiResult.Success(Unit)
                } else {
                    ApiResult.Error(DefaultApiError.UnexpectedError("An unknown error occurred while configuring DHCP interface $interfaceName"))
                }
            }catch (e: SecurityException) {
                ApiResult.Error(DefaultApiError.UnexpectedError(e.message!!))
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}