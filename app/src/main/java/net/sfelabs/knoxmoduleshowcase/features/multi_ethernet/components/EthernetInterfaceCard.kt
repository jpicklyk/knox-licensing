package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Warning

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkInterfaceState

@Composable
fun EthernetInterfaceCard(
    name: String,
    ipAddresses: String? = null,
    macAddress: String? = null,
    connected: NetworkInterfaceState = NetworkInterfaceState.Unknown
) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(5.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(connected) {
                NetworkInterfaceState.Connected -> {
                    Icon(imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Ethernet interface is available",
                        tint = Color.Green,
                        modifier = Modifier.size(40.dp)
                    )
                }
                NetworkInterfaceState.Disconnected -> {
                    Icon(imageVector = Icons.Outlined.Error,
                        contentDescription = "Ethernet interface is not available",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                }
                NetworkInterfaceState.Unknown -> {
                    Icon(imageVector = Icons.Outlined.Warning,
                        contentDescription = "Ethernet interface is in an unknown state",
                        tint = Color.Yellow,
                        modifier = Modifier.size(40.dp)
                    )

                }
            }


            Column(Modifier.padding(8.dp)) {
                Text(
                    text = "Interface ($name)",
                    style = MaterialTheme.typography.headlineSmall
                    )
                if(ipAddresses != null) {
                    Text(
                        text = "IP Address: $ipAddresses",
                        style = MaterialTheme.typography.bodyMedium
                                           )
                }
                //if(macAddress != null) {
                    Text(
                        text = "MAC Address: $macAddress",
                        style = MaterialTheme.typography.bodyMedium
                    )
                //}
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EthernetInterfaceCardDark() {
    EthernetInterfaceCard(
        name = "eth0",
        ipAddresses = "192.168.2.100",
        macAddress = "3f:e4:5j:33:4k",
        connected = NetworkInterfaceState.Unknown)
}