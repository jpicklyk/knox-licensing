package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.features.network_manager.NetworkManagerScreen

const val networkManagerNavigationRoute = "network_manager_route"

fun NavController.navigateToNetworkManager(navOptions: NavOptions? = null) {
    this.navigate(networkManagerNavigationRoute, navOptions)
}

fun NavGraphBuilder.networkManagerScreen() {
    composable(route = networkManagerNavigationRoute) {
        NetworkManagerScreen()
    }
}