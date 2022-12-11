package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.viewmodel

import android.content.Context
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
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.DhcpEthernetInterface
import net.sfelabs.knox_tactical.domain.model.EthernetInterface
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knox_tactical.domain.model.StaticEthernetInterface
import net.sfelabs.knox_tactical.domain.use_cases.tactical.ethernet.CheckInterfacesUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tactical.ethernet.ConfigureEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tactical.ethernet.GetEthernetAutoConnection
import net.sfelabs.knox_tactical.domain.use_cases.tactical.ethernet.SetEthernetAutoConnection
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services.EthernetNetworkMonitor
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.EthernetConfigurationEvents
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.EthernetConfigurationState
import javax.inject.Inject

@HiltViewModel
class EthernetConfigurationViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val configureEthernetInterfaceUseCase: ConfigureEthernetInterfaceUseCase,
    private val setEthernetAutoConnection: SetEthernetAutoConnection,
    private val getEthernetAutoConnection: GetEthernetAutoConnection,
    private val checkInterfacesUseCase: CheckInterfacesUseCase,
    private val log: net.sfelabs.android_log_wrapper.Log,
    private val ethernetMonitor: EthernetNetworkMonitor
): ViewModel() {
    private val _state = MutableStateFlow(EthernetConfigurationState(isLoading = true))
    val stateFlow: StateFlow<EthernetConfigurationState> = _state.asStateFlow()
    private lateinit var connected: EthernetNetworkMonitor.EthernetConnectionState
    private val _ethernetState = MutableStateFlow(mapOf<String,EthernetInterface>())
    val interfaces: StateFlow<Map<String,EthernetInterface>> get() = _ethernetState

    init {
        _state.update{ EthernetConfigurationState(
            isLoading = false,
            autoConnectionState = getAutoEthernetConnectionState(),
            interfaceName = "eth0",
            ipAddress = "192.168.2.123",
            netmask = "255.255.255.0",
            gateway = "192.168.2.1",
            dnsList = "192.168.2.1, 8.8.8.8"
        )}

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _ethernetState.emit(hashMapOf(Pair("eth0", DhcpEthernetInterface("eth0"))))
            }
        }

        viewModelScope.launch {
            ethernetMonitor.ethernetState
                //.catch { exception -> doSomething(exception) }
                .collect {stateUpdate ->
                                connected = stateUpdate
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun onEvent(event: EthernetConfigurationEvents) {
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
                        gateway = event.value.ifEmpty { null }
                    )}
                }
                is EthernetConfigurationEvents.EnteredDnsList -> {
                        _state.update{_state.value.copy(dnsList = event.value)}
                }
                is EthernetConfigurationEvents.EnteredInterfaceName -> {
                        _state.update{_state.value.copy(interfaceName = event.value)}
                }
                is EthernetConfigurationEvents.EnteredIpAddress -> {
                        _state.update{_state.value.copy(ipAddress = event.value)}
                }
                is EthernetConfigurationEvents.EnteredNetmask -> {
                        _state.update{_state.value.copy(netmask = event.value)}
                }

                is EthernetConfigurationEvents.SelectedInterfaceType -> {
                        _state.update{_state.value.copy(interfaceType = event.value)}
                }
                EthernetConfigurationEvents.CheckEthernetInterfaces ->
                    checkInterfacesUseCase()
            }
        }

    }


    private fun setAutoEthernetConnectionState(autoConnectionState: AutoConnectionState) {
        //settingsManager.ethernetAutoConnectionState = autoConnectionState.state
        viewModelScope.launch {
            setEthernetAutoConnection(
                autoConnectionState = autoConnectionState, listOf(getNetworkCallback())
            ).collect { result ->
                when(result) {
                    is ApiCall.Success -> {
                        if(autoConnectionState == AutoConnectionState.ON) {
                            log.d("Successfully set Auto Connection State ON")
                            _state.update{_state.value.copy(autoConnectionState = AutoConnectionState.ON)}
                        } else {
                            log.d("Successfully set Auto Connection State OFF")
                            _state.update{_state.value.copy(autoConnectionState = AutoConnectionState.OFF)}
                        }
                    }
                    is ApiCall.Error -> {
                        log.e("An error occurred while setting the Auto Connection State")
                    }
                }

            }
        }

    }

    private fun getAutoEthernetConnectionState(): AutoConnectionState {
        return AutoConnectionState(0)
    //TODO - Put this back
    //return getEthernetAutoConnection()

    }

    private fun getNetworkCallback(): NetworkCallback {
        val tag = "NETWORKCALLBACK"
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
                ethernetInterface = ethInterface,
                callback = getNetworkCallback()
            ).collect { result ->
                when(result) {
                    is ApiCall.Success -> {
                        log.d("Successfully configured ${ethInterface.name}")
                        //ethernetInterfaceRepository.saveInterface(ethInterface)
                        _ethernetState.update { _ethernetState.value.toMutableMap().also { ethMap ->
                            ethMap.put(ethInterface.name, ethInterface) }
                        }
                    }
                    is ApiCall.Error -> {
                        log.e("Error occurred while creating ${ethInterface.name} configuration")
                    }

                }
            }
        }
    }

    private fun convertStateToEthernetInterface(): EthernetInterface {
        val ethernet = _state.value
        return when(ethernet.interfaceType) {
            is EthernetInterfaceType.DHCP -> {
                DhcpEthernetInterface(
                    name = ethernet.interfaceName
                )
            }
            is EthernetInterfaceType.STATIC -> {
                StaticEthernetInterface(
                    name = ethernet.interfaceName,
                    ipAddress = ethernet.ipAddress!!,
                    netmask = ethernet.netmask!!,
                    gateway = if(ethernet.gateway?.isBlank() == true) null else ethernet.gateway,
                    dnsList = if(ethernet.dnsList.trim().isBlank())
                                    emptyList()
                                else
                                    ethernet.dnsList.split(",").map { it.trim() }
                )
            }
        }
    }
}