package net.sfelabs.common.core.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Referemce for handling snackbar with scaffold:
 * https://www.devbitsandbytes.com/configuring-snackbar-jetpack-compose-using-scaffold-with-bottom-navigation/
 */
@Composable
fun ErrorSnackbar(
    errorMessage: String,
    showError: Boolean = !errorMessage.isNullOrBlank(),
    modifier: Modifier = Modifier,
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