package net.sfelabs.knoxmoduleshowcase.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.common.knox.KnoxComponentType
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.TacticalKnoxState
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.components.KnoxApiComponent
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel.TacticalKnoxEvents
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel.TacticalTesterViewModel
import net.sfelabs.knoxmoduleshowcase.presentation.KnoxShowcaseAppState

@Composable
fun TacticalHomeScreen(appState: KnoxShowcaseAppState) {
    val viewModel: TacticalTesterViewModel = hiltViewModel()
    val state: TacticalKnoxState by viewModel.state.collectAsState()
    val errorText: String = state.errorText.asString()

    LaunchedEffect(state.hasError) {
        if( errorText.isNotBlank() ) {
            appState.snackbarHostState.showSnackbar(
                errorText,
                "Dismiss",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }
    }
    if(!state.isLoading) {
        Column {
            KnoxApiComponent(
                title = "Tactical Device Mode",
                description = "Tactical Device Mode disables all cellular communication including " +
                        "Emergency 911 services.  The device user will not be able to turn off " +
                        "Airplane Mode and only wired communication will be allowed.",
                onChanged = { viewModel.onEvent(TacticalKnoxEvents.SetTacticalDeviceMode(it)) },
                componentType = KnoxComponentType.BooleanComponent(state.isTacticalDeviceModeEnabled)
            )

            KnoxApiComponent(
                title = "Auto-Adjust Touch Sensitivity",
                description = "This method switch ON and OFF the touch sensitivity functionality in settings.",
                onChanged = { viewModel.onEvent(TacticalKnoxEvents.SetAutoTouchSensitivity(it)) },
                componentType = KnoxComponentType.BooleanComponent(state.isAutoTouchSensitivityEnabled)
            )


            KnoxApiComponent(
                title = "LTE Band Locking",
                description = "Lock the LTE to a specific network band",
                onChanged = {},
                componentType = KnoxComponentType.SpinnerComponent(false)
            )
        }
    }

}