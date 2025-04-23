package net.sfelabs.knoxmoduleshowcase.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import net.sfelabs.knoxmoduleshowcase.app.presentation.TacticalAppState
import net.sfelabs.knoxmoduleshowcase.features.about.navigation.aboutScreen
import net.sfelabs.knoxmoduleshowcase.features.home.navigation.homeNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.home.navigation.homeScreen
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.navigation.ethernetScreen
import net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.navigation.networkManagerScreen
import net.sfelabs.knoxmoduleshowcase.features.policy.policyScreen

@Composable
fun SetupNavGraph(
    state: TacticalAppState,
    padding: PaddingValues
    ) {
    NavHost(
        navController = state.navHostController,
        startDestination = homeNavigationRoute,
        modifier = Modifier.padding(padding)
    ) {
        homeScreen()
        policyScreen()
        ethernetScreen()
        networkManagerScreen()
        aboutScreen()
    }
}