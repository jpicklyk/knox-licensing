package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components


import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.core.ui.theme.AppTheme
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.NetworkInterfaceState


@Composable
fun InterfaceCard(
    modifier: Modifier = Modifier,
    name: String,
    addresses: List<String?> = emptyList(),
    macAddress: String? = null,
    isUp: Boolean,
    monitored: Boolean = false,
    status: NetworkInterfaceState = NetworkInterfaceState.Unknown
) {
    val (color, icon, interfaceStatus) = when(status) {
        NetworkInterfaceState.Connected ->
            Triple(Color.Green, Icons.Outlined.CheckCircle, "Connected")
        NetworkInterfaceState.Disconnected ->
            Triple(Color.Red, Icons.Outlined.ErrorOutline, "Disconnected")
        NetworkInterfaceState.Unknown -> Triple(Color.Gray, Icons.Outlined.WarningAmber, "Unknown")
    }

    val monitoredColor = if (monitored) Color.Green else Color.DarkGray

    OutlinedCard(
        modifier = modifier
            .padding(6.dp),
            //.defaultMinSize(200.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, monitoredColor),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$name - $macAddress", style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.padding(end = 5.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = interfaceStatus,
                    tint = color,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if(isUp) "Interface Up" else "Interface Down",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
                Text(
                    text = interfaceStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End,
            ) {
                for (address in addresses) {
                    if (address != null) {
                        Text(text = address, style = MaterialTheme.typography.bodySmall)
                    }
                }

                if(!macAddress.isNullOrBlank()) {
                    Text(text = macAddress, style = MaterialTheme.typography.bodyMedium)
                }
            }


        }
    }
}


/**
 * PREVIEWS
 */

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun InterfaceCardConnectedPreview() {
    AppTheme {
        InterfaceCard(
            name = "wlan0",
            addresses = listOf("192.168.2.100", "192.168.2.200"),
            macAddress = "3f:e4:5j:33:4k",
            isUp = true,
            status = NetworkInterfaceState.Connected
        )
    }

}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun InterfaceCardDisconnectedPreview() {
    AppTheme {
        InterfaceCard(
            name = "eth0",
            addresses = listOf("192.168.2.100"),
            macAddress = "3f:e4:5j:33:4k",
            isUp = true,
            monitored = true,
            status = NetworkInterfaceState.Disconnected
        )
    }

}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun InterfaceCardUnknownPreview() {
    AppTheme {
        InterfaceCard(
            name = "wlan0",
            addresses = listOf(),
            macAddress = "3f:e4:5j:33:4k",
            isUp = false,
            status = NetworkInterfaceState.Unknown
        )
    }

}