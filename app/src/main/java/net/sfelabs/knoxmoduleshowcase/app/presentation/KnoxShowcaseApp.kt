package net.sfelabs.knoxmoduleshowcase.app.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import net.sfelabs.core.component.AppNavigationBar
import net.sfelabs.core.component.AppNavigationBarItem
import net.sfelabs.core.component.TacticalTopAppBar
import net.sfelabs.knoxmoduleshowcase.app.presentation.navigation.SetupNavGraph
import net.sfelabs.knoxmoduleshowcase.app.presentation.navigation.TopLevelDestination
import net.sfelabs.knoxmoduleshowcase.features.permissions.isDeviceAdminGranted
import net.sfelabs.knoxmoduleshowcase.features.permissions.requestDeviceAdmin
import net.sfelabs.core.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnoxShowcaseApp() {
    AppTheme {
        val context = LocalContext.current
        LaunchedEffect(true) {
            if(!isDeviceAdminGranted(context))
                requestDeviceAdmin(context)
        }

        val appState: TacticalAppState = rememberKnoxShowcaseAppState()

        Scaffold(
            snackbarHost = {SnackbarHost(appState.snackbarHostState)},
            topBar = {
                appState.currentTopLevelDestination?.let { TacticalTopAppBar(titleResource = it.titleTextId) }
            },
            bottomBar = {
                AppBottomBar(
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentTopLevelDestination
                )
            }
        ) { innerPadding ->
            SetupNavGraph(state = appState, padding = innerPadding)
        }
    }
}

@Composable
private fun AppBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: TopLevelDestination?,
    modifier: Modifier = Modifier,
) {
    AppNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination == destination
            AppNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.image,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.image,
                        contentDescription = null,
                    )
                },
                label = { Text(destination.label) },
                //modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}