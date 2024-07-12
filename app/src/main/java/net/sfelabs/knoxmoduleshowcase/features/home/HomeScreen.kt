package net.sfelabs.knoxmoduleshowcase.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.core.component.KeyValueComposable
import net.sfelabs.core.component.OutlinedCardContainer
import net.sfelabs.knox_tactical.domain.model.TacticalEditionReleases


@Composable
internal fun HomeRoute(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    HomeScreen(
        buildNumber = viewModel.deviceBuildNumber.value,
        splVersion = viewModel.splValue.value,
        permissionStatusList = viewModel.permissionState.toList(),
        licenseStatus = viewModel.knoxActivationState.value
        )
}
@Composable
internal fun HomeScreen(
    buildNumber: String,
    splVersion: String,
    permissionStatusList: List<PermissionStatus>,
    licenseStatus: KnoxLicenseStatus
    ) {
    Column(
        modifier = Modifier
            //.padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        TacticalVersionInformation(
            buildNumber = buildNumber,
            splVersion = splVersion,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 6.dp)
        )

        OutlinedCardContainer(
            title = "Knox License Status",
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
            val modifier = Modifier
                .fillMaxWidth(0.28f)
                .height(24.dp)
            licenseStatus.maskedKey?.let {
                KeyTextComposable(
                    key = "License Key:",
                    value = it,
                    modifier = modifier
                )
            }
            licenseStatus.state?.let {
                KeyTextComposable(
                    key = "State:",
                    value = it.name,
                    modifier = modifier
                )
            }
            licenseStatus.activationDate?.let {
                KeyTextComposable(
                    key = "Date:",
                    value = it.toString(),
                    modifier = modifier
                )
            }
        }
        OutlinedCardContainer(
            title = "Knox Permission Status",
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)

        ) {
            permissionStatusList.forEach {
                if(it.name.startsWith("KNOX_")) {
                    KeyValueComposable(key = it.name) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val icon =
                                if (it.isEnabled) Icons.Filled.CheckCircle else Icons.Filled.Cancel
                            val color = if (it.isEnabled) Color.Green else Color.Red
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color
                            )
                        }
                    }
                }
            }
        }

        OutlinedCardContainer(
            title = "Android Permission Status",
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
        ) {
            permissionStatusList.forEach {
                if(!it.name.startsWith("KNOX_")) {
                    KeyValueComposable(key = it.name) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val icon =
                                if (it.isEnabled) Icons.Filled.CheckCircle else Icons.Filled.Cancel
                            val color = if (it.isEnabled) Color.Green else Color.Red
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color
                            )
                        }
                    }
                }
            }
        }


    }
}



@Composable
private fun TacticalVersionInformation(
    buildNumber: String,
    splVersion: String,
    modifier: Modifier = Modifier
) {
    OutlinedCardContainer(
        title = "Device Information",
        modifier = modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            // To ensure we can a nice alignment of the key value pairs, pass a customized
            // Modifier to all of the key/text composables.
            val modifierInternal = Modifier
                .fillMaxWidth(0.3f)
                .height(24.dp)
            KeyTextComposable(
                key = "Build Number:",
                value = buildNumber,
                modifier = modifierInternal
            )
            KeyTextComposable(
                key = "TE Version:",
                value = TacticalEditionReleases.getVersionInfo(buildNumber).description,
                modifier = modifierInternal
            )
            val rpPosition = buildNumber.length-10
            KeyTextComposable(
                key = "RP Version:",
                value = buildNumber.substring(rpPosition, rpPosition + 1),
                modifier = modifierInternal
            )
            KeyTextComposable(
                key = "SPL Version:",
                value = splVersion,
                modifier = modifierInternal
            )
        }
    }
}

@Composable
private fun KeyTextComposable(
    key: String,
    value: String,
    modifier: Modifier = Modifier
) {
    KeyValueComposable(key = key, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}

@Preview
@Composable
fun PreviewTacticalVersionInfo() {
    TacticalVersionInformation(
        buildNumber = "S911U1UEU2AWL1_B2BF",
        splVersion = "November 1, 2023"
        )
}