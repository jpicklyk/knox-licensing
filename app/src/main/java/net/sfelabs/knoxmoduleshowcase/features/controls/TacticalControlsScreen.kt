package net.sfelabs.knoxmoduleshowcase.features.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.core.domain.model.knox.KnoxFeatureValueType
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.TacticalKnoxState
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.components.KnoxApiComponent
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.components.KnoxApiTextComponent
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel.TacticalKnoxEvents
import net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.viewmodel.TacticalTesterViewModel

@Composable
fun TacticalControlsScreen() {
    val viewModel: TacticalTesterViewModel = hiltViewModel()
    val state: TacticalKnoxState by viewModel.state.collectAsState()
    val featureList by viewModel.knoxFeatureList.collectAsState()


    if(!state.isLoading) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {

            items(items = featureList, itemContent = { item ->
                when(val type = item.knoxFeatureValueType) {
                    is KnoxFeatureValueType.NoValue -> {
                        KnoxApiComponent(
                            title = item.title,
                            description = item.description,
                            onEvent = {
                                viewModel.onEvent(
                                    TacticalKnoxEvents.FeatureOnOffChanged(
                                        item.key,
                                        !item.enabled,
                                        null
                                    )
                                )
                            },
                            isFeatureSupported = item.isSupported,
                            isFeatureEnabled = item.enabled
                        )
                    }
                    is KnoxFeatureValueType.BooleanValue -> {}
                    is KnoxFeatureValueType.IntegerValue -> {}
                    is KnoxFeatureValueType.StringValue -> {
                        KnoxApiTextComponent(
                            title = item.title,
                            description = item.description,
                            isFeatureSupported = item.isSupported,
                            isFeatureEnabled = item.enabled,
                            onSwitchEvent = {
                                viewModel.onEvent(
                                    TacticalKnoxEvents.FeatureOnOffChanged(
                                        item.key,
                                        it,
                                        type.value
                                    )
                                )
                            },
                            data = type.value,
                            onDataChangeEvent = {
                                viewModel.onEvent(
                                    TacticalKnoxEvents.FeatureIntegerValueChanged(
                                        item.key,
                                        it
                                    )
                                )
                            }
                        )
                    }
                }

            })
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp)
            )
        }
    }

}

/**
 * Column {
 *             KnoxApiComponent(
 *                 title = "Tactical Device Mode",
 *                 description = "Tactical Device Mode disables all cellular communication including " +
 *                         "Emergency 911 services.  The device user will not be able to turn off " +
 *                         "Airplane Mode and only wired communication will be allowed.",
 *                 onEvent = { viewModel.onEvent(TacticalKnoxEvents.FeatureChanged(, it)) },
 *                 componentType = KnoxComponentType.BooleanComponent(state.isTacticalDeviceModeEnabled)
 *             )
 *
 *             KnoxApiComponent(
 *                 title = "Auto-Adjust Touch Sensitivity",
 *                 description = "This method switch ON and OFF the touch sensitivity functionality in settings.",
 *                 onEvent = { viewModel.onEvent(TacticalKnoxEvents.SetAutoTouchSensitivity(it)) },
 *                 componentType = KnoxComponentType.BooleanComponent(state.isAutoTouchSensitivityEnabled)
 *             )
 *
 *
 *             KnoxApiComponent(
 *                 title = "LTE Band Locking",
 *                 description = "Lock the LTE to a specific network band",
 *                 onEvent = {},
 *                 componentType = KnoxComponentType.SpinnerComponent(false)
 *             )
 *         }
 */