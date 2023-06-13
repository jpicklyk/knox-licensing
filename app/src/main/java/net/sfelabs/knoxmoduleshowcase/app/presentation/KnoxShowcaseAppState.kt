package net.sfelabs.knoxmoduleshowcase.app.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Application level state container that can be passed down the composition tree to various
 * screens if needed.
 */
class KnoxShowcaseAppState(
    val snackbarHostState: SnackbarHostState,
    val snackbarScope: CoroutineScope,
    val navHostController: NavHostController
) {

    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        snackbarScope.launch {
            snackbarHostState.showSnackbar(message, duration = duration)
        }
    }
}

@Composable
fun rememberKnoxShowcaseAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navHostController: NavHostController = rememberNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope()
)  = remember(snackbarHostState, navHostController, snackbarScope) {
    KnoxShowcaseAppState(
        snackbarHostState = snackbarHostState,
        navHostController = navHostController,
        snackbarScope = snackbarScope
    )
}
