package net.sfelabs.knoxmoduleshowcase.features.logviewer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.features.logviewer.SampleLogViewerScreen

const val loggerNavigationRoute = "logger_route"

fun NavController.navigateToLogger(navOptions: NavOptions? = null) {
    this.navigate(loggerNavigationRoute, navOptions)
}

fun NavGraphBuilder.loggerScreen() {
    composable(route = loggerNavigationRoute) {
        SampleLogViewerScreen()
    }
}