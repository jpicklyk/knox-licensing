package net.sfelabs.knoxmoduleshowcase.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.presentation.KnoxShowcaseAppState
import net.sfelabs.knoxmoduleshowcase.presentation.SampleLogViewerScreen
import net.sfelabs.knoxmoduleshowcase.presentation.screens.AboutScreen
import net.sfelabs.knoxmoduleshowcase.presentation.screens.EthernetConfigurationScreen
import net.sfelabs.knoxmoduleshowcase.presentation.screens.TacticalHomeScreen

@Composable
fun SetupNavGraph(
    state: KnoxShowcaseAppState,
    padding: PaddingValues
    ) {
    NavHost(
        navController = state.navHostController,
        startDestination = NavRoute.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(route = NavRoute.Home.route) {
            //Box(modifier = Modifier.fillMaxSize())
            //val viewModel: LogTextViewModel = hiltViewModel()
            //SampleLogViewerScreen()
            //EthernetConfigurationScreen()
            TacticalHomeScreen(state)
        }
        composable(route = NavRoute.Ethernet.route) {
            EthernetConfigurationScreen()
        }
        composable(route = NavRoute.Logging.route) {
            SampleLogViewerScreen()
        }
        composable(route = NavRoute.About.route) {
            AboutScreen()
        }
    }
}