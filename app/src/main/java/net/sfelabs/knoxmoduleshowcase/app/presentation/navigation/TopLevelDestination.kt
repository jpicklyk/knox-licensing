package net.sfelabs.knoxmoduleshowcase.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Policy
import androidx.compose.ui.graphics.vector.ImageVector
import net.sfelabs.knoxmoduleshowcase.R

enum class TopLevelDestination(
    val titleTextId: Int,
    val image: ImageVector,
    val label: String
) {
    HOME(
        titleTextId = R.string.app_name,
        image = Icons.Filled.Home,
        label = "Home"
    ),
    CONTROLS(
        titleTextId = R.string.controls_title,
        image = Icons.Filled.Policy,
        label = "Policies"
    ),

    ETHERNET(
        titleTextId = R.string.ethernet_title,
        image = Icons.Filled.Lan,
        label = "Ethernet"
    ),

    NetworkManager(
        titleTextId = R.string.network_monitor_title,
        image = Icons.Filled.Lan,
        label = "Monitor"
    ),
    ABOUT(
        titleTextId = R.string.about_title,
        image = Icons.Filled.Info,
        label = "Info"
    )
}