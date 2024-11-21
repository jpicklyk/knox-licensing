package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.DhcpConfiguration
import net.sfelabs.knox_tactical.domain.model.EthernetConfiguration
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knox_tactical.domain.model.StaticConfiguration
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ConfigureEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressForInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.SetEthernetAutoConnectionUseCase
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.EthernetInterface
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkInterfaceState
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.NetworkConnectivityService
import java.net.NetworkInterface
import javax.inject.Inject

@HiltViewModel
class EthernetConfigurationViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    //private val connectivityManager: ConnectivityManager,
    private val configureEthernetInterfaceUseCase: ConfigureEthernetInterfaceUseCase,
    private val setEthernetAutoConnectionUseCase: SetEthernetAutoConnectionUseCase,
    private val getEthernetAutoConnection: GetEthernetAutoConnectionUseCase,
    private val getEthernetMacAddressUseCase: GetMacAddressForInterfaceUseCase,
    private val log: net.sfelabs.android_log_wrapper.Log,
    private val networkService: NetworkConnectivityService
): ViewModel() {
    private val tag = "EthernetConfigurationVM"
    private val _state = MutableStateFlow(EthernetConfigurationState(isLoading = true))
    val stateFlow: StateFlow<EthernetConfigurationState> = _state.asStateFlow()
    private val _ethernetState = MutableStateFlow(mapOf<String, EthernetInterface>())
    private val interfaceHandleMap = HashMap<Long, String>()
    val interfaces: StateFlow<Map<String, EthernetInterface>> get() = _ethernetState

    init {
        _state.update{ EthernetConfigurationState(
            isLoading = false,
            autoConnectionState = AutoConnectionState.OFF,
            ethInterface = EthernetInterface(
                name = "eth0",
                type = EthernetInterfaceType.DHCP,
                netmask = "255.255.255.0",
                gateway = "192.168.2.1",
                dnsList = "192.168.2.1, 8.8.8.8"
            )
        )
        }

        getAutoEthernetConnectionState()
        /*
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _ethernetState.emit(hashMapOf(
                    Pair("eth0", EthernetInterface("eth0", EthernetInterfaceType.DHCP)))
                )
            }
        }

         */

        //Collect the connection state updates

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                networkService.networkUpdate.collect { networkUpdate ->
                    // Handle the network update
                    updateInterfaceConnectivityState(networkUpdate)
                }
            }
        }
        //networkService.registerInterface("eth0")
        //networkService.registerInterface("eth2")

       //val usbInterfaces = getAssignedEthernetInterfaces(connectivityManager)



    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun onEthernetConfigurationEvent(event: EthernetConfigurationEvents) {
        viewModelScope.launch {
            when(event) {
                EthernetConfigurationEvents.SaveConfiguration -> {
                    configureEthernet(convertStateToEthernetInterface())
                }
                EthernetConfigurationEvents.DisableEthernetAutoConnection ->
                    setAutoEthernetConnectionState(AutoConnectionState.OFF)
                EthernetConfigurationEvents.EnableEthernetAutoConnection ->
                    setAutoEthernetConnectionState(AutoConnectionState.ON)

                is EthernetConfigurationEvents.EnteredDefaultGateway -> {
                    _state.update{_state.value.copy(
                        ethInterface = _state.value.ethInterface
                            .copy(gateway = event.value.ifEmpty { null })
                    )}
                }
                is EthernetConfigurationEvents.EnteredDnsList -> {
                        _state.update{_state.value.copy(
                            ethInterface = _state.value.ethInterface.copy(dnsList = event.value)
                        )}
                }
                is EthernetConfigurationEvents.EnteredInterfaceName -> {
                        _state.update{_state.value.copy(
                            ethInterface = _state.value.ethInterface.copy(name = event.value)
                        )}
                }
                is EthernetConfigurationEvents.EnteredIpAddress -> {
                        _state.update{_state.value.copy(
                            ethInterface = _state.value.ethInterface.copy(ipAddress = event.value)
                        )}
                }
                is EthernetConfigurationEvents.EnteredNetmask -> {
                        _state.update{_state.value.copy(
                            ethInterface = _state.value.ethInterface.copy(netmask = event.value)
                        )}
                }

                is EthernetConfigurationEvents.SelectedInterfaceType -> {
                        _state.update{_state.value.copy(
                            ethInterface = _state.value.ethInterface.copy(type = event.value)
                        )}
                }

            }
        }

    }


    private fun setAutoEthernetConnectionState(autoConnectionState: AutoConnectionState) {
        //settingsManager.ethernetAutoConnectionState = autoConnectionState.state
        viewModelScope.launch {
            setEthernetAutoConnectionUseCase(
                autoConnectionState = autoConnectionState, listOf(getNetworkCallback())
            ).collect { result ->
                when(result) {
                    is ApiResult.Success -> {
                        if(autoConnectionState == AutoConnectionState.ON) {
                            log.d("Successfully set Auto Connection State ON")
                            _state.update{_state.value.copy(autoConnectionState = AutoConnectionState.ON)}
                        } else {
                            log.d("Successfully set Auto Connection State OFF")
                            _state.update{_state.value.copy(autoConnectionState = AutoConnectionState.OFF)}
                        }
                    }
                    is ApiResult.Error -> {
                        log.e("An error occurred while setting the Auto Connection State")
                    }

                    is ApiResult.NotSupported -> {
                        log.e("setEthernetAutoConnection method is not supported")
                    }
                }

            }
        }

    }

    private fun getAutoEthernetConnectionState() {
        when(val result: ApiResult<AutoConnectionState> = getEthernetAutoConnection()) {
            is ApiResult.Success -> {
                _state.update{_state.value.copy(autoConnectionState = result.data)}
            }
            is ApiResult.Error -> {
                log.e(result.apiError.message)
            }

            is ApiResult.NotSupported -> {
                log.e("getEthernetAutoConnection method is not supported")
            }
        }
    }

    private fun getNetworkCallback(): NetworkCallback {
        val tag = "NETWORK-CALLBACK"
        return object: NetworkCallback() {
            override fun onAvailable(network : Network) {
                super.onAvailable(network)
                Log.d(tag,"Network available: $network")
                showToast("Network available: $network")
            }

            override fun onUnavailable() {
                Log.d(tag,"Network unavailable")
                showToast("Network unavailable")
                super.onUnavailable()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                Log.w(tag,"Losing network: $network")
                super.onLosing(network, maxMsToLive)
            }

            override fun onLost(network : Network) {
                showToast("Lost network: $network")
                Log.w(tag,"Lost network: $network")
                super.onLost(network)
            }

            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                Log.d(tag,"The network changed capabilities: $networkCapabilities")
            }

            override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
                Log.d(tag,"The default network changed link properties: $linkProperties")
            }
        }
    }

    private fun configureEthernet(ethInterface: EthernetInterface) {
        viewModelScope.launch {
            configureEthernetInterfaceUseCase(
                ethernetInterface = toEthernetConfiguration(ethInterface),
                callback = getNetworkCallback()
            ).collect { result ->
                when(result) {
                    is ApiResult.Success -> {
                        log.d("Successfully configured ${ethInterface.name}")
                        //ethernetInterfaceRepository.saveInterface(ethInterface)
                        _ethernetState.update {
                            _ethernetState.value.toMutableMap().also {
                                    ethMap -> ethMap[ethInterface.name] = ethInterface
                            }
                        }

                    }
                    is ApiResult.Error -> {
                        log.e("Error occurred while creating ${ethInterface.name} configuration")
                    }

                    is ApiResult.NotSupported -> {
                        log.e("configureEthernet method is not supported")
                    }
                }
            }
        }
    }

    private fun convertStateToEthernetInterface(): EthernetInterface {
        return _state.value.ethInterface
    }

    private fun toEthernetConfiguration(eth: EthernetInterface): EthernetConfiguration {
        return when(eth.type) {
            is EthernetInterfaceType.DHCP ->
                DhcpConfiguration(
                    name = eth.name
                )
            is EthernetInterfaceType.STATIC ->
                StaticConfiguration(
                    name = eth.name,
                    ipAddress = eth.ipAddress?: "Unknown",
                    gateway = eth.gateway,
                    netmask = eth.netmask?: "Unknown",
                    dnsList = eth.dnsList?.split(",")?.map { it.trim() } ?: emptyList()
                )
        }
    }
    private fun updateInterfaceConnectivityState(networkUpdate: NetworkUpdate) {
        Log.d(tag, "State Changed: Interface [${networkUpdate.handle}] ${networkUpdate.interfaceName} is ${networkUpdate.isConnected}")
        if(networkUpdate.isConnected == NetworkInterfaceState.Connected) {
            interfaceHandleMap[networkUpdate.handle] = networkUpdate.interfaceName!!
        }
        val interfaceName = interfaceHandleMap[networkUpdate.handle]
        if(interfaceName != null) {
            val mac = getEthernetMacAddress(interfaceName)
            val updatedInterfaces = _ethernetState.value.toMutableMap()
            val existingInterface = updatedInterfaces[interfaceName]

            if (existingInterface != null) {
                val updatedInterface = existingInterface.copy(
                    connectivity = networkUpdate.isConnected,
                    mac = mac,
                    ipAddress = networkUpdate.ipAddress
                    )
                updatedInterfaces[interfaceName] = updatedInterface
                networkService.registerInterface(interfaceName)
                _ethernetState.value = updatedInterfaces.toMap()
            } else {
                val newInterface = EthernetInterface(
                    name = interfaceName,
                    type = EthernetInterfaceType.DHCP,
                    ipAddress = networkUpdate.ipAddress,
                    mac = mac,
                    connectivity = networkUpdate.isConnected
                )
                updatedInterfaces[interfaceName] = newInterface
                _ethernetState.value = updatedInterfaces.toMap()
            }
        }

        if(networkUpdate.isConnected == NetworkInterfaceState.Disconnected) {
            interfaceHandleMap.remove(networkUpdate.handle)
        }
    }

    private fun getEthernetMacAddress(name: String?): String? {
        if(name == null)
            return null
        val result = getEthernetMacAddressUseCase.invoke(name)
        return if(result is ApiResult.Success) {
            result.data
        } else {
            "(Api Not Supported)"
        }
    }

    private fun getAssignedEthernetInterfaces(connectivityManager: ConnectivityManager): List<String> {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val assignedInterfaces = mutableListOf<String>()

        if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true) {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (isEthernetInterface(networkInterface)) {
                    assignedInterfaces.add(networkInterface.name)
                }
            }
        }

        return assignedInterfaces
    }

    private fun isEthernetInterface(networkInterface: NetworkInterface): Boolean {
        val interfaceName = networkInterface.name
        return interfaceName.startsWith("eth", ignoreCase = true) or interfaceName.startsWith("rndis", ignoreCase = true)
    }

}