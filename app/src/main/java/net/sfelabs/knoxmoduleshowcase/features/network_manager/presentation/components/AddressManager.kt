package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfigurationType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.netmaskToPrefix


@Composable
fun AddressManager(
    title: String,
    itemList: List<InterfaceAddressConfiguration>,
    onAddressSelected: (InterfaceAddressConfiguration?) -> Unit = {},
    onAddAddressClick: () -> Unit = {},
    onModifyAddressClick: () -> Unit = {},
    onDeleteAddressClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        var selectedIndex by remember { mutableIntStateOf(-1) }

        val onItemClick = {index: Int ->
            selectedIndex =
                if(selectedIndex == index) {
                    onAddressSelected(null)
                    -1
                } else {
                    onAddressSelected(itemList[index])
                    index
                }
        }

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp
            ),
            text = title)
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(itemList) { index, item ->
                    ItemView(
                        index,
                        item,
                        selected = selectedIndex == index,
                        onClick = onItemClick
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Column {
                OutlinedButton(
                    modifier = Modifier.widthIn(min = 100.dp),
                    onClick = {
                        onAddAddressClick()
                        selectedIndex = -1
                    },
                ) {
                    Text(text = "Add")
                }
                OutlinedButton(
                    modifier = Modifier.widthIn(min = 100.dp),
                    onClick = onModifyAddressClick,
                    enabled = selectedIndex != -1
                ) {
                    Text(text = "Modify")
                }
                OutlinedButton(
                    modifier = Modifier.widthIn(min = 100.dp),
                    onClick = {
                        onDeleteAddressClick()
                        selectedIndex = -1
                    },
                    enabled = selectedIndex != -1
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun ItemView(index: Int, item: InterfaceAddressConfiguration, selected: Boolean, onClick: (Int) -> Unit) {
    val defaultColor = MaterialTheme.colorScheme.secondaryContainer
    val selectedColor = MaterialTheme.colorScheme.tertiaryContainer

    val prefix = netmaskToPrefix(item.netmask ?: "")
    val postfix = if(item.type == InterfaceAddressConfigurationType.DHCP) "(dynamic)" else "(static)"
    Text(
        text = "${item.ipAddress}/$prefix $postfix",
        modifier = Modifier
            .clickable { onClick(index) }
            .background(if (selected) selectedColor else defaultColor)
            .fillMaxWidth()
            .padding(8.dp)
    )
}



@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IpConfiguratorPreview() {
    AddressManager(
        title = "IPv4 Addresses",
        itemList = listOf(
            InterfaceAddressConfiguration(
                index = 0,
                type = InterfaceAddressConfigurationType.Static,
                "192.168.1.1", "255.255.255.0"
            ),
            InterfaceAddressConfiguration(
                index = 1,
                type = InterfaceAddressConfigurationType.DHCP,
                "192.168.1.100", "255.255.255.0",
                dnsList = listOf("8.8.8.8", "8.8.4.4")
            )
        )


    )
}