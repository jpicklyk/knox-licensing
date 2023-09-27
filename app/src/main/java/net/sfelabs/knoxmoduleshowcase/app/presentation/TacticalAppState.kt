package net.sfelabs.knoxmoduleshowcase.app.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sfelabs.knoxmoduleshowcase.app.presentation.navigation.TopLevelDestination
import net.sfelabs.knoxmoduleshowcase.features.about.navigation.aboutNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.about.navigation.navigateToAbout
import net.sfelabs.knoxmoduleshowcase.features.controls.navigation.controlsNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.controls.navigation.navigateToControls
import net.sfelabs.knoxmoduleshowcase.features.home.navigation.homeNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.home.navigation.navigateToHome
import net.sfelabs.knoxmoduleshowcase.features.logviewer.navigation.loggerNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.logviewer.navigation.navigateToLogger
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.navigation.ethernetNavigationRoute
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.navigation.navigateToEthernet

/**
 * Application level state container that can be passed down the composition tree to various
 * screens if needed.
 */


@Composable
fun rememberKnoxShowcaseAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navHostController: NavHostController = rememberNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope()
)  = remember(snackbarHostState, navHostController, snackbarScope) {
    TacticalAppState(
        snackbarHostState = snackbarHostState,
        navHostController = navHostController,
        snackbarScope = snackbarScope
    )
}

class TacticalAppState(
    val snackbarHostState: SnackbarHostState,
    val snackbarScope: CoroutineScope,
    val navHostController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navHostController.currentBackStackEntryAsState().value?.destination



    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeNavigationRoute -> TopLevelDestination.HOME
            controlsNavigationRoute -> TopLevelDestination.CONTROLS
            ethernetNavigationRoute -> TopLevelDestination.ETHERNET
            loggerNavigationRoute -> TopLevelDestination.LOGGING
            aboutNavigationRoute -> TopLevelDestination.ABOUT
            else -> null
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.HOME -> navHostController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.CONTROLS -> navHostController.navigateToControls(topLevelNavOptions)
            TopLevelDestination.ETHERNET -> navHostController.navigateToEthernet(topLevelNavOptions)
            TopLevelDestination.LOGGING -> navHostController.navigateToLogger(topLevelNavOptions)
            TopLevelDestination.ABOUT -> navHostController.navigateToAbout(topLevelNavOptions)
        }

    }
    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        snackbarScope.launch {
            snackbarHostState.showSnackbar(message, duration = duration)
        }
    }
}
