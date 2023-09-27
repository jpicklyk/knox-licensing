package net.sfelabs.knoxmoduleshowcase.features.controls.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.features.controls.TacticalControlsScreen

const val controlsNavigationRoute = "controls_route"

fun NavController.navigateToControls(navOptions: NavOptions? = null) {
    this.navigate(controlsNavigationRoute, navOptions)
}

fun NavGraphBuilder.controlsScreen() {
    composable(route = controlsNavigationRoute) {
        TacticalControlsScreen()
    }
}