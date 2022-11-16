package net.sfelabs.knoxmoduleshowcase.presentation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import net.sfelabs.knoxmoduleshowcase.presentation.navigation.NavBarItems
import net.sfelabs.knoxmoduleshowcase.presentation.navigation.SetupNavGraph
import net.sfelabs.knoxmoduleshowcase.ui.theme.KnoxModuleShowcaseTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnoxShowcaseApp() {
    KnoxModuleShowcaseTheme {
        val appState: KnoxShowcaseAppState = rememberKnoxShowcaseAppState()
        var selectedItem by remember { mutableStateOf(0) }

        Scaffold(
            snackbarHost = {SnackbarHost(appState.snackbarHostState)},
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Knox Playground ") },
                    /*
                    navigationIcon = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                     */
                )
            },
            bottomBar = {
                NavigationBar {
                    NavBarItems.BarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index, 
                            onClick = {
                                selectedItem = index
                                val nav = appState.navHostController
                                nav.navigate(item.navRoute.route) {
                                    popUpTo(nav.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                      },
                            icon = { Icon(item.image, contentDescription = item.title) },
                            label = { Text(text = item.title)}
                        )
                    }
                }
            }
        ) { innerPadding ->
            SetupNavGraph(state = appState, padding = innerPadding)
        }
    }
}

