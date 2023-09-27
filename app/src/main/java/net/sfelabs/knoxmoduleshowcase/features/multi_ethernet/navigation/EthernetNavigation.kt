package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.EthernetConfigurationScreen

const val ethernetNavigationRoute = "ethernet_route"

fun NavController.navigateToEthernet(navOptions: NavOptions? = null) {
    this.navigate(ethernetNavigationRoute, navOptions)
}

fun NavGraphBuilder.ethernetScreen() {
    composable(route = ethernetNavigationRoute) {
        EthernetConfigurationScreen()
    }
}