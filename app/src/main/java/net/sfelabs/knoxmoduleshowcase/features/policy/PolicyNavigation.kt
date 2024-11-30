package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val policyNavigationRoute = "policy_route"

fun NavController.navigateToPolicy(navOptions: NavOptions? = null) {
    this.navigate(policyNavigationRoute, navOptions)
}

fun NavGraphBuilder.policyScreen() {
    composable(route = policyNavigationRoute) {
        PolicyScreen()
    }
}