package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType

import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.EthernetConfigurationViewModel


@Composable
fun EthernetList(
    viewModel: EthernetConfigurationViewModel = hiltViewModel()

) {
   //TODO - Make proper data class(es) to tie in all the various ethernet properties
   val ethernetMap by viewModel.interfaces.collectAsState()
   LazyColumn(
       modifier = Modifier.fillMaxWidth(),
       contentPadding = PaddingValues(16.dp)
   ) {
       item { 
           Row(modifier = Modifier.fillMaxWidth()) {
               Text(text = "Configured Interfaces")
           }
       }
       items(ethernetMap.values.toList(), key = {it.name}) {
           //val connected: Boolean = it.connectivity == ConnectivityState.Available
           when(it.type) {
               is EthernetInterfaceType.DHCP -> {
                   EthernetInterfaceCard(
                       name = it.name,
                       ipAddresses = it.ipAddress,
                       macAddress = it.mac,
                       connected = it.connectivity
                   )
               }
               is EthernetInterfaceType.STATIC -> {
                   EthernetInterfaceCard(
                       name = it.name,
                       ipAddresses = it.ipAddress,
                       macAddress = it.mac,
                       connected = it.connectivity
                   )
               }
           }

       }
   }
}
