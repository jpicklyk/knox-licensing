package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.internals

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.NetworkConnectivityService
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EthernetNetworkService @Inject constructor(
    connectivityManager: ConnectivityManager
): NetworkConnectivityService {
    private val tag = "EthernetNetworkService"

    override val networkUpdate: Flow<net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate> = callbackFlow {
        val connectivityCallback = object: NetworkCallback() {

            override fun onAvailable(network: Network) {
                val linkProperties =
                    connectivityManager.getLinkProperties(network)
                val interfaceName = linkProperties?.interfaceName
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is connected")
                trySend(
                    net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate(
                        interfaceName,
                        ipAddress = getIpv4Address(linkProperties),
                        network.networkHandle,
                        net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkStatus.Connected
                    )
                )
            }

            override fun onLost(network: Network) {
                val linkProperties =
                    connectivityManager.getLinkProperties(network)
                val interfaceName = linkProperties?.interfaceName
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is disconnected")
                trySend(
                    net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate(
                        interfaceName,
                        ipAddress = getIpv4Address(linkProperties),
                        network.networkHandle,
                        net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkStatus.Disconnected
                    )
                )
            }
        }

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, connectivityCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    private fun getIpv4Address(linkProperties: LinkProperties?): String? {
        if (linkProperties != null) {
            for (linkAddress in linkProperties.linkAddresses) {
                val address = linkAddress.address
                if (address is Inet4Address) {
                    return address.hostAddress
                }
            }
        }
        return null
    }
}