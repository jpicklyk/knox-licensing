package net.sfelabs.knoxmoduleshowcase.features.policy.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.core.knox.feature.api.BooleanPolicyState
import net.sfelabs.core.knox.feature.api.PolicyState
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.core.ui.theme.AppTheme
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.model.LteNrMode
import net.sfelabs.knox_tactical.domain.model.NightVisionState
import net.sfelabs.knox_tactical.domain.policy.auto_call_pickup.AutoCallPickupState
import net.sfelabs.knox_tactical.domain.policy.band_locking.BandLockingState
import net.sfelabs.knox_tactical.domain.policy.nr_mode.NrModeState
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent

@Composable
fun PolicyCard(
    policy: PolicyUiState,
    onEvent: (PolicyEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local UI state for tracking edits
    val currentEdits = remember(policy) {
        mutableStateOf(
            when (policy) {
                is PolicyUiState.ConfigurableToggle -> policy.configurationOptions
                else -> emptyMap()
            }
        )
    }
    var hasUnsavedChanges = remember { mutableStateOf(false) }

    val showSaveButton = policy is PolicyUiState.ConfigurableToggle &&
            policy.configurationOptions.isNotEmpty()

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title and Controls section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = policy.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showSaveButton) {
                        Button(
                            onClick = {
                                if (policy is PolicyUiState.ConfigurableToggle) {
                                    createPolicyState(
                                        policy = policy,
                                        config = currentEdits.value,
                                        isEnabled = policy.isEnabled
                                    )?.let { newState ->
                                        onEvent(PolicyEvent.UpdatePolicy(
                                            policyState = newState,
                                            featureName = policy.featureName
                                        ))
                                        hasUnsavedChanges.value = false
                                    }
                                }
                            },
                            enabled = hasUnsavedChanges.value &&
                                    policy.isEnabled &&
                                    policy.isSupported
                        ) {
                            Text("Save")
                        }
                    }
                    Switch(
                        checked = policy.isEnabled,
                        onCheckedChange = { isEnabled ->
                            when (policy) {
                                is PolicyUiState.Toggle -> {
                                    onEvent(PolicyEvent.UpdatePolicy(
                                        BooleanPolicyState(isEnabled = isEnabled),
                                        policy.featureName
                                    ))
                                }
                                is PolicyUiState.ConfigurableToggle -> {
                                    createPolicyState(
                                        policy = policy,
                                        config = currentEdits.value,
                                        isEnabled = isEnabled
                                    )?.let { newState ->
                                        onEvent(
                                            PolicyEvent.UpdatePolicy(newState, policy.featureName)
                                        )
                                    }
                                }
                            }
                            // Ensuring that the save button is disabled on change of switch
                            hasUnsavedChanges.value = false
                        },
                        enabled = policy.isSupported
                    )
                }
            }

            // Description
            Text(
                text = policy.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Error and Support Status
            if (!policy.isSupported) {
                Text(
                    text = "(This feature is not supported on this device)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            policy.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Loading indicator
            if (policy.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Configuration Options
            if (policy is PolicyUiState.ConfigurableToggle &&
                policy.isSupported
                //policy.isEnabled &&
                //policy.configurationOptions.isNotEmpty()
            ) {
                HorizontalDivider()
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PolicyConfiguration(
                        options = currentEdits.value,
                        onConfigChange = { key, value ->
                            currentEdits.value = currentEdits.value.toMutableMap().apply {
                                put(key, value)
                            }
                            hasUnsavedChanges.value = true
                        }
                    )
                }
            }
        }
    }
}

// Helper function to create appropriate PolicyState
private fun createPolicyState(
    policy: PolicyUiState.ConfigurableToggle,
    config: Map<String, Any?>,
    isEnabled: Boolean
): PolicyState? {
    return when (policy.featureName) {
        "auto_call_pickup_policy" -> {
            val mode = config["mode"] as? AutoCallPickupMode ?: AutoCallPickupMode.Enable
            AutoCallPickupState(
                isEnabled = isEnabled,
                mode = mode,
            )
        }
        "band_locking" -> {
            val band = config["band"] as? Int ?: return null
            val simSlotId = config["simSlotId"] as? Int
            BandLockingState(
                isEnabled = isEnabled,
                band = band,
                simSlotId = simSlotId
            )
        }
        "nr_mode_policy" -> {
            val mode = config["mode"] as? LteNrMode ?: LteNrMode.EnableBothSaAndNsa
            val simSlotId = config["simSlotId"] as? Int
            NrModeState(
                isEnabled = isEnabled,
                mode = mode,
                simSlotId = simSlotId
            )

        }
        "night_vision" -> {
            val useRedOverlay = config["useRedOverlay"] as? Boolean == true
            NightVisionState(
                isEnabled = isEnabled,
                useRedOverlay = useRedOverlay
            )
        }
        else -> null
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PolicyCardPreview() {
    AppTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                featureName = "nr_mode",
                isEnabled = true,
                isSupported = true,
                isLoading = false,
                error = null,
                configurationOptions = mapOf(
                    "simSlotId" to 0,
                    "mode" to "Enable Both SA and NSA"
                )
            ),
            onEvent = {},
            modifier = Modifier,
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PolicyCardErrorPreview() {
    AppTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                featureName = "nr_mode",
                isEnabled = false,
                isSupported = true,
                isLoading = false,
                error = "Failed to update policy",
                configurationOptions = mapOf(
                    "simSlotId" to 0,
                    "mode" to "Enable Both SA and NSA"
                )
            ),
            onEvent = {},
            modifier = Modifier,
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PolicyCardUnsupportedPreview() {
    AppTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                featureName = "nr_mode",
                isEnabled = false,
                isSupported = false,
                isLoading = false,
                error = null,
                configurationOptions = mapOf(
                    "simSlotId" to 0,
                    "mode" to "Enable Both SA and NSA"
                )
            ),
            onEvent = {},
            modifier = Modifier,
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PolicyCardLoadingPreview() {
    AppTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                featureName = "nr_mode",
                isEnabled = true,
                isSupported = true,
                isLoading = true,
                error = null,
                configurationOptions = mapOf(
                    "simSlotId" to 0,
                    "mode" to "Enable Both SA and NSA"
                )
            ),
            onEvent = {},
            modifier = Modifier,
        )
    }
}