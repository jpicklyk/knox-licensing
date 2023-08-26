package net.sfelabs.knox_tactical.domain.use_cases.ethernet

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.EthernetNetworkSpecifier
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureDhcpEthernetInterface
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.configureStaticEthernetInterface
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.EthernetConfiguration
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knox_tactical.domain.model.StaticConfiguration
import javax.inject.Inject

class ConfigureEthernetInterfaceUseCase @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    @TacticalSdk private val systemManager: SystemManager
) {
    /**
     * Generally we are performing this function several times (minimum of 2 or more when using
     * greater than 2 ethernet connections) so I will expose this as a flow scoped to the ViewScope
     * for efficiency reasons.
     *
     * To remove a particular eth interface all you need to do is unregister the NetworkCallback
     * that was used for it.
     */
    @SuppressLint("MissingPermission")
    operator fun invoke(
        ethernetInterface: EthernetConfiguration,
        callback: NetworkCallback
    ): Flow<UnitApiCall> = flow {
        val success: Boolean =
            when(ethernetInterface.type) {
                is EthernetInterfaceType.DHCP -> {
                    systemManager.configureDhcpEthernetInterface(ethernetInterface.name)
                }
                is EthernetInterfaceType.STATIC -> {
                    val static = ethernetInterface as StaticConfiguration
                    systemManager.configureStaticEthernetInterface(
                        static.name,
                        static.ipAddress,
                        static.dnsList,
                        static.gateway,
                        static.netmask
                    )
                }
            }


        if(success) {
            //Android 12 changed the way permissions work and now we need to request each network
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                requestNetwork(ethernetInterface.name, callback)
            else
                registerNetworkCallback(ethernetInterface.name, callback)

            emit(ApiCall.Success(Unit))
        } else emit(
            ApiCall.Error(
            uiText = UiText.DynamicString("An unknown error occurred while configuring DHCP " +
                    "interface ${ethernetInterface.name}"))
        )

    }.flowOn(Dispatchers.IO)

    /**
     * Due to permission changes in Android 12, you need to perform a request network
     * call in order to tie the necessary permissions to your application. The
     * <class>NetworkCallback</class> object can be empty or you may utilize flows to monitor
     * for conditional changes such as onLost or onAvailable.
     */
    private fun requestNetwork(interfaceName: String, networkCallback: NetworkCallback) {
        val networkRequest = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            buildNetworkRequestTiramisu(interfaceName)
        } else {
            buildNetworkRequest(interfaceName)
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    private fun registerNetworkCallback(interfaceName: String, callback: NetworkCallback) {
        val networkRequest = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            buildNetworkRequestTiramisu(interfaceName)
        } else {
            buildNetworkRequest(interfaceName)
        }
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildNetworkRequestTiramisu(interfaceName: String): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .setNetworkSpecifier(EthernetNetworkSpecifier(interfaceName))
            .build()
    }
    @Suppress("DEPRECATION")
    private fun buildNetworkRequest(interfaceName: String): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            // You need to specify the ethernet interface name to associate
            .setNetworkSpecifier(interfaceName)
            .build()
    }
}