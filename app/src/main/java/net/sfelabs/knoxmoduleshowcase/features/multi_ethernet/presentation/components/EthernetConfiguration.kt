package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.EthernetConfigurationEvents
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.EthernetConfigurationState
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation.viewmodel.EthernetConfigurationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthernetConfiguration(
    modifier: Modifier = Modifier,
    viewModel: EthernetConfigurationViewModel = hiltViewModel()

) {
    val state: EthernetConfigurationState by viewModel.stateFlow.collectAsState()
    var expandedState by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    OutlinedCard(

        modifier = modifier
            .padding(4.dp)
            .verticalScroll(scrollState),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.End
        ) {
            if(expandedState) {
                Icon(imageVector = Icons.Filled.ExpandLess, null)
            } else {
                Icon(imageVector = Icons.Filled.ExpandMore, null)
            }

        }
        if(!state.isLoading){
            Row {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(8.dp),
                    onClick = {viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnableEthernetAutoConnection)},
                    enabled = (!state.isAutoConnectionEnabled())
                ) {
                    Text(text = "Enable Ethernet")
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.DisableEthernetAutoConnection)},
                    enabled = (state.isAutoConnectionEnabled())
                ) {
                    Text(text = "Disable Ethernet")
                }
            }
        }

        EthernetTypeComponent(
            radioOptions = listOf(EthernetInterfaceType.DHCP, EthernetInterfaceType.STATIC),
            interfaceName = state.ethInterface.name,
            selectedInterfaceType = state.ethInterface.type,
            onInterfaceNameChange = { name ->
                viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnteredInterfaceName(name))
            },
            onSelectInterfaceType = { type ->
                if(type is EthernetInterfaceType.STATIC) {
                    expandedState = true
                }
                viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.SelectedInterfaceType(type))
            }
        )
        StaticIPFieldsComponent(
            state = state,
            isVisible = expandedState,
            onIpAddressChanged = { viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnteredIpAddress(it)) },
            onNetmaskChanged = { viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnteredNetmask(it)) },
            onGatewayChanged = { viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnteredDefaultGateway(it)) },
            onDnsListChanged = { viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.EnteredDnsList(it)) }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {viewModel.onEthernetConfigurationEvent(EthernetConfigurationEvents.SaveConfiguration)}
        ) {
            Text(
                text = "Configure Interface"
                )
        }
    }
}


@Composable
fun EthernetTypeComponent(
    radioOptions: List<EthernetInterfaceType> = listOf(),
    interfaceName: String,
    selectedInterfaceType: EthernetInterfaceType,
    onInterfaceNameChange: (String) -> Unit,
    onSelectInterfaceType: (EthernetInterfaceType) -> Unit

) {
    Row(
        Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        Column(modifier = Modifier
            .width(150.dp)
            .padding(top = 12.dp)
        ) {
            OutlinedTextField(
                value = interfaceName,
                onValueChange = onInterfaceNameChange,
                label = { Text(text = "Interface name") }
            )
            Text(text = "eth0, eth1, etc",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                )
        }
        Column(
            modifier = Modifier
                .padding(start = 9.dp)
                .fillMaxWidth()
        ) {
            radioOptions.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item == selectedInterfaceType),
                        onClick = { onSelectInterfaceType(item) }
                    )

                    val annotatedString = buildAnnotatedString {
                        append("${item.interfaceType}  ")
                    }
                    Text(
                        text = "${item.interfaceType}  ",
                        modifier = Modifier
                            .clickable(
                                enabled = true,
                                role = Role.Button
                            ) {
                                onSelectInterfaceType(item)
                            }
                    )
                }

        }

            /*
            ClickableText(
                text = annotatedString,
                onClick = {
                    onSelectInterfaceType(item)
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Col
                )
            )

             */
        }
    }

}

@Composable
fun StaticIPFieldsComponent(
    state: EthernetConfigurationState,
    isVisible: Boolean = true,
    onIpAddressChanged: (String) -> Unit,
    onNetmaskChanged: (String) -> Unit,
    onGatewayChanged: (String) -> Unit,
    onDnsListChanged: (String) -> Unit
) {
    if(isVisible) {
        Row {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                OutlinedTextField(
                    value = state.ethInterface.ipAddress ?: "",
                    onValueChange = onIpAddressChanged,
                    label = { Text(text = "IP Address") }
                )
                OutlinedTextField(
                    value = state.ethInterface.netmask ?: "",
                    onValueChange = onNetmaskChanged,
                    label = { Text(text = "Netmask") }
                )
                OutlinedTextField(
                    value = state.ethInterface.gateway ?: "",
                    onValueChange = onGatewayChanged,
                    label = { Text(text = "Default Gateway") }
                )
                OutlinedTextField(
                    value = state.ethInterface.dnsList?: "",
                    onValueChange = onDnsListChanged,
                    label = { Text(text = "DNS List") }
                )
            }
        }
    }
}

/*
@Preview
@Composable
fun PreviewEthernetConfiguration() {
    EthernetConfiguration(
        modifier = Modifier
            .fillMaxSize(),
        onConfigureClick = {},
        onAutoConnectionClick = {},

    )
}

 */