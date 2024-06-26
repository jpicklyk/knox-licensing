package net.sfelabs.knoxmoduleshowcase.features.network_manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.InterfaceListPane
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.NetworkManagerListViewModel
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components.AddressConfigurationDetails
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components.AddressManager
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components.InterfaceConfigurationDetails


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NetworkManagerScreen(
) {
    val viewModel: NetworkManagerListViewModel = hiltViewModel()
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            InterfaceListPane(
                navigator = navigator,
                configurations = state.interfaces,
                interfaceStates = state.interfaceMap,
                onInterfaceClick = viewModel::onInterfaceConfigurationSelected,
                onFabClick = viewModel::onNewNetworkClicked
            )},
        detailPane = {
            val readOnly = navigator.currentDestination?.content as Boolean? ?: false
            val content = state.selectedInterfaceConfiguration

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if(content != null) {
                    InterfaceConfigurationDetails(
                        readOnly = readOnly,
                        configurationName = content.configurationName,
                        interfaceName = content.interfaceName,
                        interfaceType = content.interfaceType,
                        macAddress = content.macAddress,
                        interfaceMonitored = content.isInterfaceMonitored,
                        onMonitoredChanged = viewModel::onInterfaceConfigurationMonitoredChanged,
                        onInterfaceTypeSelected = viewModel::onInterfaceTypeChanged,
                        onConfigurationNameChange = viewModel::onConfigurationNameChanged,
                        onInterfaceNameChange = viewModel::onInterfaceNameChanged,
                    )
                    AddressManager(
                        title = "IPv4 Addresses",
                        itemList = content.ipAddresses,
                        onAddressSelected = viewModel::onInterfaceAddressConfigurationSelected,
                        onAddAddressClick = {
                            viewModel.onAddNewInterfaceAddressConfiguration()
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra)
                        },
                        onModifyAddressClick = {
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Extra)
                        },
                        onDeleteAddressClick = {
                            viewModel.onDeleteInterfaceAddressConfiguration()
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilledTonalButton(
                            onClick = {
                                viewModel.onSaveInterfaceConfiguration(content)
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.List)
                            },
                            modifier = Modifier.widthIn(min = 100.dp)
                        ) {
                            Text("Save")
                        }
                        FilledTonalButton(
                            onClick = {
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.List)
                            },
                            modifier = Modifier.widthIn(min = 100.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        },
        extraPane = {
            val content = state.selectedInterfaceAddressConfiguration
            val interfaceType = state.selectedInterfaceConfiguration?.interfaceType
            if(content != null && interfaceType != null) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AddressConfigurationDetails(
                        interfaceType = interfaceType,
                        type = content.type,
                        onAddressTypeChanged = viewModel::onInterfaceConfigurationTypeChanged,
                        ipAddress = content.ipAddress,
                        onIpAddressChanged = viewModel::onIpAddressChanged,
                        netmask = content.netmask,
                        onNetmaskChanged = viewModel::onNetmaskChanged,
                        gateway = content.gateway,
                        onGatewayChanged = viewModel::onGatewayChanged,
                        dnsList = content.dnsList.joinToString(", "),
                        onDnsListChanged = viewModel::onDnsListChanged,
                        onOkClicked = {
                            viewModel.onSaveInterfaceAddressConfigurationChanges()
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        },
                        onCancelClicked = {
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledTonalButton(
                        onClick = {
                            //TODO - Save selected configuration
                        },
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Text("OK")
                    }
                    FilledTonalButton(
                        onClick = {
                            navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        },
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        },
    )

}
