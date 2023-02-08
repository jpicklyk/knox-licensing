package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.os.Build
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EthernetNetworkMonitor @Inject constructor(
    private val connectivityManager: ConnectivityManager
) {
    private val defaultName = "NotAvailableYet"
    private val tag = "ETHERNET_MONITOR"
    private val interfaceMap = ConcurrentHashMap<Long, EthernetConnectionState>(4)

    val ethernetState: Flow<EthernetConnectionState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.i(tag, "Network ${network.networkHandle} available")
                if(!interfaceMap.containsKey(network.networkHandle)) {
                    Log.e(tag,"The network ${network.networkHandle} is not mapped!")
                    interfaceMap.put(
                        network.networkHandle,
                        EthernetConnectionState(
                            defaultName,
                            network.networkHandle,
                            ConnectivityState.Available
                        )
                    )
                } else {
                    Log.i(tag,"The network ${network.networkHandle} is mapped and available.")
                    val state = interfaceMap.get(network.networkHandle)
                    trySend(
                        EthernetConnectionState(state!!.name, state.id, ConnectivityState.Available)
                    )
                }
            }

            override fun onLost(network: Network) {
                Log.i(tag, "Network ${network.networkHandle} lost")
                val state = interfaceMap.remove(network.networkHandle)
                if (state != null) {
                    trySend(
                        EthernetConnectionState(state.name, state.id, ConnectivityState.Unavailable)
                    )
                }
                super.onLost(network)
            }

            override fun onCapabilitiesChanged(
                network : Network,
                networkCapabilities : NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                Log.d(tag,"The network ${network.networkHandle} changed capabilities: " +
                        "\n$networkCapabilities")
                if(!interfaceMap.containsKey(network.networkHandle)) {
                    Log.w(tag,"The network ${network.networkHandle} is not mapped, mapping now")
                    interfaceMap.put(
                        network.networkHandle,
                        buildInterfaceState(network.networkHandle, networkCapabilities)
                    )
                }
                val state = interfaceMap.get(network.networkHandle)
                //If the current interface name is unknown, update it and push the event over the flow
                if(state!!.name == defaultName) {
                    Log.i(tag, "Network ${network.networkHandle} found with default name")
                    interfaceMap.put(
                        network.networkHandle,
                        buildInterfaceState(
                            network.networkHandle, networkCapabilities, state.connectivity
                        )
                    )
                    interfaceMap.get(network.networkHandle)?.let { trySend(it) }
                }
            }
        }
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }


    fun buildInterfaceState(
        id: Long,
        networkCapabilities: NetworkCapabilities,
        connectivity: ConnectivityState = ConnectivityState.Unavailable
    ): EthernetConnectionState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EthernetConnectionState(
                name = decodeInterfaceName(networkCapabilities.networkSpecifier),
                id = id
            )
        } else {
            TODO("VERSION.SDK_INT < R")
        }
    }

    data class EthernetConnectionState(
        val name: String,
        val id: Long,
        val connectivity: ConnectivityState = ConnectivityState.Unavailable
        )

    //Requires api level 30
    private fun decodeInterfaceName(specifier: NetworkSpecifier?): String {
        return specifier?.toString()
            ?.substringAfter("(")?.substringBefore(")") ?: "Unknown"
    }
}

sealed class ConnectivityState {
    object Available: ConnectivityState()
    object Unavailable: ConnectivityState()
}