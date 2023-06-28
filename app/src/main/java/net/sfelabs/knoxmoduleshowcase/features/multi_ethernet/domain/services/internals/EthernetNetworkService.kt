package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.internals

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.EthernetNetworkSpecifier
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkStatus
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.NetworkConnectivityService
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EthernetNetworkService @Inject constructor(
    connectivityManager: ConnectivityManager
): NetworkConnectivityService {
    private val tag = "EthernetNetworkService"
    private val registeredInterfaceList = mutableListOf<String>()
    private val _registrationNotifierFlow = MutableStateFlow("ALL")
    private val registeredInterfaces: StateFlow<String> = _registrationNotifierFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val networkUpdate: Flow<NetworkUpdate> = registeredInterfaces.flatMapMerge {ethernetName ->
        flow {
                Log.d(tag, "Setting up network request for $ethernetName ethernet interface(s)")
                emitAll(createNetworkUpdateFlow(connectivityManager, ethernetName))
        }
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)


    private fun createNetworkUpdateFlow(connectivityManager: ConnectivityManager, ifaceName: String): Flow<NetworkUpdate> = callbackFlow {
        val connectivityCallback = object : NetworkCallback() {
            // Network callback methods
            override fun onAvailable(network: Network) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                val interfaceName = linkProperties?.interfaceName
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is connected")
                trySend(
                    NetworkUpdate(
                        interfaceName,
                        ipAddress = getIpv4Address(linkProperties),
                        network.networkHandle,
                        NetworkStatus.Connected
                    )
                )
            }

            override fun onLost(network: Network) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                val interfaceName = linkProperties?.interfaceName
                Log.d(tag, "Interface [${network.networkHandle}] $interfaceName is disconnected")
                trySend(
                    NetworkUpdate(
                        interfaceName,
                        ipAddress = getIpv4Address(linkProperties),
                        network.networkHandle,
                        NetworkStatus.Disconnected
                    )
                )
            }
        }

        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)

        // Dynamically add registered interfaces to the NetworkRequest
        if(ifaceName != "ALL") {
            Log.d(tag, "Building network request for $ifaceName")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                buildNetworkRequestTiramisu(requestBuilder, ifaceName)
            } else {
                buildNetworkRequest(requestBuilder, ifaceName)
            }
        }

        val request = requestBuilder.build()
        connectivityManager.registerNetworkCallback(request, connectivityCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }
    }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildNetworkRequestTiramisu(networkRequest: NetworkRequest.Builder, interfaceName: String): NetworkRequest.Builder {
        return networkRequest.setNetworkSpecifier(EthernetNetworkSpecifier(interfaceName))
    }
    @Suppress("DEPRECATION")
    private fun buildNetworkRequest(networkRequest: NetworkRequest.Builder, interfaceName: String) {
        networkRequest.setNetworkSpecifier(interfaceName)
    }


    override fun registerInterface(interfaceName: String) {
        if(registeredInterfaceList.contains(interfaceName))
            return
        Log.d(tag, "Registering interface $interfaceName")
        registeredInterfaceList.add(interfaceName)
        _registrationNotifierFlow.update {
            interfaceName
        }
    }

}