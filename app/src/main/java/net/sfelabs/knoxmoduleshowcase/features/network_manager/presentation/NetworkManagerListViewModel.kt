package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetMacAddressForInterfaceUseCase
import net.sfelabs.knoxmoduleshowcase.features.network_manager.NetworkManagerState
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.Interface
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfigurationType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType.Bridge.toInterfaceType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.NetworkInterfaceState
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.inetAddressesToConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.ipv4Addresses
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.toInterface
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.updatedIpv4AddressList
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.service.EthernetConfigurationManager
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.service.NetworkMonitorService
import java.net.NetworkInterface
import javax.inject.Inject


@HiltViewModel
class NetworkManagerListViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val getMacAddressForInterfaceUseCase: GetMacAddressForInterfaceUseCase,
    private val ethernetConfigurationManager: EthernetConfigurationManager
): ViewModel() {
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val _state = MutableStateFlow(NetworkManagerState())
    val state: StateFlow<NetworkManagerState> get() = _state


    private val monitoredInterfaceDefaults: ArrayList<String> =
        arrayListOf("eth0", "eth1", "eth2", "wlan0", "usb0", "rndis0", "br0")

    init {
        scanCurrentInterfaces()
        observeNetworkState()
        startNetworkMonitorService()
        //pollInterfacesForUpdates()
    }

    fun onNewNetworkClicked() {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceConfiguration = InterfaceConfiguration(isInterfaceMonitored = true)
            )
        }
    }

    fun onInterfaceConfigurationSelected(configuration: InterfaceConfiguration) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceConfiguration = configuration
            )
        }
    }

    fun onInterfaceNameChanged(name: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceConfiguration = currentState.selectedInterfaceConfiguration?.copy(
                    interfaceName = name
                )
            )}
    }

    fun onInterfaceTypeChanged(type: InterfaceType) {
        _state.update { currentState ->
            // Ensure that the interface name is prefixed correctly for the interface type
            // e.g. eth for Ethernet
            val currentConfig = currentState.selectedInterfaceConfiguration
            val newName = currentConfig?.interfaceName
                ?.takeUnless { it.isBlank() || !it.startsWith(type.namePrefix) } ?: type.namePrefix
            currentState.copy(
                selectedInterfaceConfiguration = currentState.selectedInterfaceConfiguration?.copy(
                    interfaceType = type,
                    interfaceName = newName
                )
            )
        }
    }

    fun onInterfaceConfigurationMonitoredChanged(monitored: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceConfiguration = currentState.selectedInterfaceConfiguration?.copy(
                    isInterfaceMonitored = monitored
                )
            )
        }
    }

    fun onConfigurationNameChanged(name: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceConfiguration = currentState.selectedInterfaceConfiguration?.copy(
                    configurationName = name
                )
            )
        }
    }

    fun onAddNewInterfaceAddressConfiguration() {
        _state.update { currentState ->
            val newIndex = currentState.selectedInterfaceConfiguration?.ipAddresses?.size ?: 0
            val newAddress = InterfaceAddressConfiguration(
                index = newIndex,
                type = InterfaceAddressConfigurationType.Static,
                ipAddress = "",
                netmask = "255.255.255.0",
                gateway = "",
                dnsList = emptyList()
            )
            currentState.copy(
                selectedInterfaceAddressConfiguration = newAddress,
                selectedInterfaceConfiguration = currentState.selectedInterfaceConfiguration?.copy(
                    ipAddresses = currentState.selectedInterfaceConfiguration.ipAddresses + newAddress
                )
            )
        }
    }

    fun onSaveInterfaceAddressConfigurationChanges() {
        _state.update { currentState ->
            val selectedConfig = currentState.selectedInterfaceConfiguration ?: return@update currentState
            val selectedAddress = currentState.selectedInterfaceAddressConfiguration ?: return@update currentState
            val updatedAddresses = selectedConfig.ipAddresses.toMutableList()
            updatedAddresses[selectedAddress.index] = selectedAddress

            currentState.copy(
                selectedInterfaceConfiguration = selectedConfig.copy(
                    ipAddresses = updatedAddresses
                )
            )
        }
    }

    fun onDeleteInterfaceAddressConfiguration() {
        _state.update { currentState ->
            val selectedConfig =
                currentState.selectedInterfaceConfiguration ?: return@update currentState
            val selectedAddress =
                currentState.selectedInterfaceAddressConfiguration ?: return@update currentState
            val updatedAddresses = selectedConfig.ipAddresses - selectedAddress

            currentState.copy(
                selectedInterfaceConfiguration = selectedConfig.copy(
                    ipAddresses = updatedAddresses
                ),
                selectedInterfaceAddressConfiguration = null
            )
        }
    }

    fun onInterfaceAddressConfigurationSelected(address: InterfaceAddressConfiguration?) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration = address
            )
        }
    }

    fun onInterfaceConfigurationTypeChanged(type: InterfaceAddressConfigurationType) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration =
                currentState.selectedInterfaceAddressConfiguration?.copy(
                    type = type
                )
            )
        }
    }

    fun onIpAddressChanged(ipAddress: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration = currentState.selectedInterfaceAddressConfiguration?.copy(
                    ipAddress = ipAddress
                )
            )
        }
    }

    fun onNetmaskChanged(netmask: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration = currentState.selectedInterfaceAddressConfiguration?.copy(
                    netmask = netmask
                )
            )
        }
    }

    fun onGatewayChanged(gateway: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration = currentState.selectedInterfaceAddressConfiguration?.copy(
                    gateway = gateway
                )
            )
        }
    }

    fun onDnsListChanged(dnsList: String) {
        _state.update { currentState ->
            currentState.copy(
                selectedInterfaceAddressConfiguration = currentState.selectedInterfaceAddressConfiguration?.copy(
                    dnsList = dnsList.split(",").map { it.trim() }
                )
            )
        }
    }

    fun onSaveInterfaceConfiguration(interfaceConfiguration: InterfaceConfiguration) {
        val (isCurrentlyMonitored, index) = isInterfaceMonitoredIndexed(interfaceConfiguration.interfaceName)
        _state.update { currentState ->
            if (isCurrentlyMonitored) {
                currentState.copy(
                    interfaces = currentState.interfaces.toMutableList().also {
                        it[index] = interfaceConfiguration
                    }
                )
            } else {
                currentState.copy(
                    interfaces = currentState.interfaces.toMutableList().also {
                        it.add(interfaceConfiguration)
                    }
                )
            }
        }
        configureNetworkInterface(interfaceConfiguration)
    }

    /**
     * Because the ConnectionManager will not handle RNDIS, Bridge, or Tunnel interfaces, we need to
     * poll the interfaces for updates.
     */
    private fun pollInterfacesForUpdates() {
        viewModelScope.launch {
            while (true) {
                NetworkInterface.getNetworkInterfaces().toList()
                    .filter { it.name.matches(Regex("^(rndis|br|ipip|eth)[0-8]", RegexOption.IGNORE_CASE))}
                    .forEach {
                        addOrUpdateInterfaceMapping(it.name, it.toInterface())
                        val (monitored, index) = isInterfaceMonitoredIndexed(it.name)
                        if (monitored) addInterfaceConfiguration(it.name, it.toInterface())
                        else updateInterfaceConfigurationFromStateChange(index,it.toInterface())
                    }
                delay(5000)
            }
        }
    }

    /**
     * Load the current interfaces from the system.  Only interfaces that have an address will be
     * seen.  Function filters out the lo and dummy0 interfaces which shouldn't be used.
     */
    private fun scanCurrentInterfaces() {
        NetworkInterface.getNetworkInterfaces().toList()
            .filter {it.name != "lo" && it.name != "dummy0"}
            .forEach {
                addOrUpdateInterfaceMapping(it.name, it.toInterface())
                val (monitored, index) = isInterfaceMonitoredIndexed(it.name)
                if (!monitored) addInterfaceConfiguration(it.name, it.toInterface())
                else updateInterfaceConfigurationFromStateChange(index,it.toInterface())
            }
    }

    private fun observeNetworkState() {
        serviceScope.launch(Dispatchers.Default) {
                NetworkMonitorService.getNetworkStateFlow().collect {
                (interfaceName, state) ->
                    Log.d("NETWORK-MONITOR",
                        "Received network status update for: $interfaceName, is up? ${state.isUp}, State: ${state.networkStatus}")
                    addOrUpdateInterfaceMapping(interfaceName, state)
                    val (monitored, index) = isInterfaceMonitoredIndexed(interfaceName)
                    if (!monitored) addInterfaceConfiguration(interfaceName, state)
                    else updateInterfaceConfigurationFromStateChange(index, state)
                }
        }
    }

    private fun startNetworkMonitorService() {
        NetworkMonitorService.startService(context, monitoredInterfaceDefaults)
    }

    private fun isInterfaceMonitoredIndexed(interfaceName: String): Pair<Boolean, Int> {
        val index = _state.value.interfaces.indexOfFirst { it.interfaceName == interfaceName }
        return Pair(index != -1, index)
    }

    /**
     * Called when the ConnectivityManager sends an interface state change when there
     * is no corresponding InterfaceConfiguration.
     */
    private fun addInterfaceConfiguration(interfaceName: String, interfaceState: Interface) {
        val macAddress = lookupMacAddress(interfaceName)
        _state.update { currentState ->
            val newConfig = InterfaceConfiguration(
                interfaceType = interfaceState.toInterfaceType(),
                interfaceName = interfaceName,
                configurationName = "$interfaceName - $macAddress",
                ipAddresses = inetAddressesToConfiguration(interfaceState.ipv4Addresses()),
                macAddress = macAddress,
                isInterfaceMonitored = false
            )

            val updatedInterfaces = currentState.interfaces + newConfig
            currentState.copy(
                interfaceMap = currentState.interfaceMap + (interfaceName to interfaceState),
                interfaces = updatedInterfaces
            )
        }

    }


    private fun addOrUpdateInterfaceMapping(interfaceName: String, interfaceState: Interface) {
        _state.update { currentState ->
            currentState.copy(
                interfaceMap = currentState.interfaceMap.toMutableMap().apply {
                    put(interfaceName, interfaceState)
                }
            )
        }
    }

    /**
     * To be used to update the current state of the Interface configuration.
     */
    private fun updateInterfaceConfigurationFromStateChange(index: Int, interfaceState: Interface) {
        if(index == -1) return

        if (interfaceState.networkStatus == NetworkInterfaceState.Connected) {
             _state.update { currentState ->
                val configuration = currentState.interfaces[index]
                val macAddress = if(configuration.macAddress.isNullOrBlank())
                    lookupMacAddress(interfaceState.name)
                else configuration.macAddress

                val updatedInterface = configuration.copy(
                    ipAddresses = configuration.updatedIpv4AddressList(interfaceState.interfaceAddresses),
                    macAddress = macAddress,
                )

                currentState.copy(
                    interfaces = currentState.interfaces.toMutableList().also {
                        it[index] = updatedInterface
                    }
                )
            }
        }
    }

    private fun removeInterface(interfaceName: String) {
        _state.update { currentState ->
            currentState.copy(
                interfaceMap = currentState.interfaceMap - interfaceName
            )
        }
    }

    private fun lookupMacAddress(interfaceName: String): String {
        val result = getMacAddressForInterfaceUseCase(interfaceName)
        return if (result is ApiResult.Success) {
            result.data
        } else {
            ""
        }
    }

    private fun configureNetworkInterface(configuration: InterfaceConfiguration) {
        if (!configuration.isInterfaceMonitored) return

        viewModelScope.launch {
            if (configuration.interfaceType == InterfaceType.Ethernet) {
                ethernetConfigurationManager.applyConfiguration(_state.value.interfaces)
            }
        }
    }
}