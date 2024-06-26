package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.Interface
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.NetworkInterfaceState
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.addressList
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components.InterfaceCard

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun InterfaceListPane(
    navigator: ThreePaneScaffoldNavigator<Any>,
    configurations: List<InterfaceConfiguration>,
    interfaceStates: Map<String, Interface>,
    onInterfaceClick: (InterfaceConfiguration) -> Unit,
    onFabClick: () -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onFabClick()
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, false)
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Interface")
            }
        }
    ) {
        innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(configurations) { configuration ->
                val isUp = interfaceStates[configuration.interfaceName]?.isUp ?: false
                val netStatus = interfaceStates[configuration.interfaceName]?.networkStatus
                    ?: NetworkInterfaceState.Unknown
                InterfaceCard(
                    name = configuration.interfaceName,
                    addresses = configuration.addressList(),
                    macAddress = configuration.macAddress,
                    status = netStatus,
                    isUp = isUp,
                    monitored = configuration.isInterfaceMonitored,
                    modifier = Modifier.clickable {
                        onInterfaceClick(configuration)
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            content = true
                        )
                    }
                )
            }
        }
    }
}
