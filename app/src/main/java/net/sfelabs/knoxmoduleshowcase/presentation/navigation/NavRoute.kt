package net.sfelabs.knoxmoduleshowcase.presentation.navigation

sealed class NavRoute(val route: String) {
    object Home: NavRoute("home_screen")
    object Ethernet: NavRoute("ethernet_screen")
    object Logging: NavRoute("logging_screen")
    object About: NavRoute("about_screen")
}
