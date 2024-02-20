package net.sfelabs.core.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Reference for handling snack bar with scaffold:
 * https://www.devbitsandbytes.com/configuring-snackbar-jetpack-compose-using-scaffold-with-bottom-navigation/
 */
@Composable
fun ErrorSnackbar(
    modifier: Modifier = Modifier,
    errorMessage: String,
    showError: Boolean = errorMessage.isNotBlank(),
    onErrorAction: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    val snackbarHostState = remember {SnackbarHostState()}

    LaunchedEffect(showError) {
        snackbarHostState.showSnackbar(
            message = errorMessage,
            actionLabel = "Dismiss",
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
    }
    SnackbarHost(hostState = snackbarHostState)
}