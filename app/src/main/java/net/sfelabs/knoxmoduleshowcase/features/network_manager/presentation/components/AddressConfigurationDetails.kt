package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samsung.sea.jpicklyk.tacticalqa.core.designsystem.theme.ApplicationTheme
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfigurationType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType

@Composable
fun AddressConfigurationDetails(
    interfaceType: InterfaceType,
    type: InterfaceAddressConfigurationType = InterfaceAddressConfigurationType.Static,
    onAddressTypeChanged: (InterfaceAddressConfigurationType) -> Unit = {},
    ipAddress: String,
    onIpAddressChanged: (String) -> Unit = {},
    netmask: String,
    onNetmaskChanged: (String) -> Unit = {},
    gateway: String? = null,
    onGatewayChanged: (String) -> Unit = {},
    dnsList: String = "",
    onDnsListChanged: (String) -> Unit = {},
    onOkClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val readOnly: Boolean = type == InterfaceAddressConfigurationType.DHCP
        val enabled = !readOnly
        Row {
            OutlinedTextField(
                modifier = Modifier.widthIn(min = 200.dp),
                value = ipAddress,
                onValueChange = onIpAddressChanged,
                enabled = enabled,
                readOnly = readOnly,
                label = { Text(text = "IP Address") }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            AddressTypeSelector(
                selectedAddressType = type,
                onAddressTypeChanged = onAddressTypeChanged
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = netmask,
            onValueChange = onNetmaskChanged,
            enabled = enabled,
            readOnly = readOnly,
            label = { Text(text = "Netmask") }
        )
        if(!readOnly) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = gateway ?: "",
                onValueChange = onGatewayChanged,
                label = { Text(text = "Gateway") }
            )

            if(interfaceType == InterfaceType.Ethernet) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = dnsList,
                    onValueChange = onDnsListChanged,
                    label = { Text(text = "DNS list") }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(
                onClick = onOkClicked,
                modifier = Modifier.widthIn(min = 100.dp)
            ) {
                Text("OK")
            }
            FilledTonalButton(
                onClick = onCancelClicked,
                modifier = Modifier.widthIn(min = 100.dp)
            ) {
                Text("Cancel")
            }
        }

    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddressConfigurationDetailsPreview() {
    ApplicationTheme {
        Surface {
            AddressConfigurationDetails(
                interfaceType = InterfaceType.Ethernet,
                type = InterfaceAddressConfigurationType.Static,
                ipAddress = "192.168.1.1",
                netmask = "255.255.255.0",
                gateway = "192.168.1.1",
                dnsList = "8.8.8.8, 8.8.4.4",
                onIpAddressChanged = {},
                onNetmaskChanged = {},
                onGatewayChanged = {},
                onDnsListChanged = {},
                onOkClicked = {},
                onCancelClicked = {}
            )
        }
    }
}