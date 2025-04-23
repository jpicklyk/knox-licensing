package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samsung.sea.jpicklyk.tacticalqa.core.designsystem.theme.ApplicationTheme
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType

@Composable
fun InterfaceConfigurationDetails(
    modifier: Modifier = Modifier,
    configurationName: String,
    interfaceName: String,
    interfaceType: InterfaceType,
    interfaceMonitored: Boolean = false,
    onMonitoredChanged: (Boolean) -> Unit = {},
    macAddress: String?,
    onInterfaceTypeSelected: (InterfaceType) -> Unit = {},
    onConfigurationNameChange: (String) -> Unit = {},
    onInterfaceNameChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {

    Column(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.spacedBy(6.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "Monitor Network?")
            Checkbox(interfaceMonitored, {onMonitoredChanged(!interfaceMonitored)})
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = configurationName,
            onValueChange = onConfigurationNameChange,
            label = { Text(text = "Configuration Name") }
        )
        InterfaceTypeSelector(
            selectedInterfaceType = interfaceType,
            onInterfaceTypeSelected = onInterfaceTypeSelected,
            readOnly = readOnly
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier.widthIn(min = 120.dp),
                value = interfaceName,
                readOnly = readOnly,
                onValueChange = onInterfaceNameChange,
                label = { Text(text = "Interface Name") },
            )

            OutlinedTextField(
                modifier = Modifier.widthIn(min = 185.dp),
                value = macAddress ?: "<Not available>",
                readOnly = true,
                onValueChange = onInterfaceNameChange,
                label = { Text(text = "MAC Address") },
            )
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
fun InterfaceDetailsPreview() {
    ApplicationTheme {
        Surface {
            val name = "eth0"
            val mac = "00:11:22:33:44:55"
            InterfaceConfigurationDetails(
                configurationName = "$name - $mac",
                interfaceName = name,
                interfaceType = InterfaceType.Ethernet,
                macAddress = mac,
                onConfigurationNameChange = {},
            )
        }
    }
}
