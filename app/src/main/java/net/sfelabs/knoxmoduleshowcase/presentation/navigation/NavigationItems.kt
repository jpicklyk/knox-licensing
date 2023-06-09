package net.sfelabs.knoxmoduleshowcase.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Policy
import androidx.compose.ui.graphics.vector.ImageVector

data class BarItem(
    val title: String,
    val image: ImageVector,
    val navRoute: NavRoute
)

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            navRoute = NavRoute.Home
        ),
        BarItem(
            title = "Controls",
            image = Icons.Filled.Policy,
            navRoute = NavRoute.Home
        ),
        BarItem(
            title = "Ethernet",
            image = Icons.Filled.Lan,
            navRoute = NavRoute.Ethernet
        ),
        BarItem(
            title = "Logging",
            image = Icons.Filled.EventNote,
            navRoute = NavRoute.Logging
        ),
        BarItem(
            title = "About",
            image = Icons.Filled.Info,
            navRoute = NavRoute.About
        )
    )
}