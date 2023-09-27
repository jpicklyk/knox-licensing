package net.sfelabs.knoxmoduleshowcase.features.about.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import net.sfelabs.knoxmoduleshowcase.features.about.AboutScreen
import net.sfelabs.knoxmoduleshowcase.features.about.AboutScreenViewModel

const val aboutNavigationRoute = "about_route"

fun NavController.navigateToAbout(navOptions: NavOptions? = null) {
    this.navigate(aboutNavigationRoute, navOptions)
}

fun NavGraphBuilder.aboutScreen() {
    composable(route = aboutNavigationRoute) {
        val viewModel = viewModel<AboutScreenViewModel>()
        AboutScreen(viewModel.informationState.value)
    }
}