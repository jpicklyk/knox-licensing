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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samsung.sea.jpicklyk.tacticalqa.core.designsystem.theme.ApplicationTheme
import kotlinx.coroutines.delay
import net.sfelabs.knox.core.feature.ui.model.ConfigurationOption
import net.sfelabs.knox.core.feature.ui.model.PolicyUiState
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent

@Composable
fun PolicyCard(
    policy: PolicyUiState,
    onEvent: (PolicyEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Local UI state for tracking edits
    var pendingOptions = remember(policy) { mutableStateOf(policy.currentOptions()) }
    val hasUnsavedChanges = remember(policy, pendingOptions.value) {
        derivedStateOf {
            policy is PolicyUiState.ConfigurableToggle &&
                    policy.configurationOptions != pendingOptions.value
        }
    }

    // Type-safe update callbacks
    val updateToggleOption = remember(policy) { { option: ConfigurationOption.Toggle, checked: Boolean ->
        pendingOptions.value = pendingOptions.value.map {
            if (it.key == option.key) option.copy(isEnabled = checked) else it
        }
    }}

    val updateChoiceOption = remember(policy) { { option: ConfigurationOption.Choice, selected: String ->
        pendingOptions.value = pendingOptions.value.map {
            if (it.key == option.key) option.copy(selected = selected) else it
        }
    }}

    val updateNumberOption = remember(policy) { { option: ConfigurationOption.NumberInput, value: Int ->
        pendingOptions.value = pendingOptions.value.map {
            if (it.key == option.key) option.copy(value = value) else it
        }
    }}

    val updateTextInputOption = remember(policy) { { option: ConfigurationOption.TextInput, value: String ->
        pendingOptions.value = pendingOptions.value.map {
            if (it.key == option.key) option.copy(value = value) else it
        }
    }}

    val updateTextListOption = remember(policy) { { option: ConfigurationOption.TextList, values: Set<String> ->
        pendingOptions.value = pendingOptions.value.map {
            if (it.key == option.key) option.copy(values = values) else it
        }
    }}

    // Debounced loading state
    var showLoading by remember { mutableStateOf(false) }

    LaunchedEffect(policy.isLoading) {
        if (policy.isLoading) {
            // Only show loading indicator if the operation takes longer than 150ms
            delay(150)
            showLoading = true
        } else {
            showLoading = false
        }
    }

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
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (policy is PolicyUiState.ConfigurableToggle && hasUnsavedChanges.value) {
                        Button(
                            onClick = {
                                onEvent(PolicyEvent.UpdateConfiguration(
                                    featureName = policy.policyName,
                                    newUiState = policy.copy(
                                        configurationOptions = pendingOptions.value
                                    )
                                ))
                            },
                            enabled = policy.isEnabled && policy.isSupported
                        ) {
                            Text("Save")
                        }
                    }
                    Switch(
                        checked = policy.isEnabled,
                        onCheckedChange = { isEnabled ->
                            when (policy) {
                                is PolicyUiState.Toggle -> onEvent(
                                    PolicyEvent.UpdateConfiguration(
                                        featureName = policy.policyName,
                                        newUiState = policy.copy(isEnabled = isEnabled)
                                    )
                                )
                                is PolicyUiState.ConfigurableToggle -> onEvent(
                                    PolicyEvent.UpdateConfiguration(
                                        featureName = policy.policyName,
                                        newUiState = policy.copy(
                                            isEnabled = isEnabled,
                                            configurationOptions = pendingOptions.value
                                        )
                                    )
                                )
                            }
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
            if (showLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Configuration Options for ConfigurableToggle
            if (policy is PolicyUiState.ConfigurableToggle &&
                policy.isSupported &&
                policy.configurationOptions.isNotEmpty()
            ) {
                HorizontalDivider()
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Configuration",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    pendingOptions.value.forEach { option ->
                        when (option) {
                            is ConfigurationOption.Toggle -> ConfigurationCheckbox(
                                label = option.label,
                                checked = option.isEnabled,
                                onCheckedChange = { checked ->
                                    updateToggleOption(option, checked)
                                },
                                enabled = option.isSupported
                            )
                            is ConfigurationOption.Choice -> ConfigurationDropdown(
                                label = option.label,
                                selected = option.selected,
                                options = option.options,
                                onSelectionChange = { selected ->
                                    updateChoiceOption(option, selected)
                                }
                            )
                            is ConfigurationOption.NumberInput -> ConfigurationNumber(
                                label = option.label,
                                value = option.value,
                                range = option.range,
                                onValueChange = { value ->
                                    updateNumberOption(option, value)
                                }
                            )
                            is ConfigurationOption.TextInput -> ConfigurationTextInput(
                                label = option.label,
                                value = option.value,
                                hint = option.hint,
                                onValueChange = { value ->
                                    updateTextInputOption(option, value)
                                }
                            )
                            is ConfigurationOption.TextList -> ConfigurationTextList(
                                label = option.label,
                                values = option.values,
                                hint = option.hint,
                                onRemove = { item ->
                                    updateTextListOption(option, option.values - item)
                                }
                            )
                        }
                    }
                }
            }
        }
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
    ApplicationTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                policyName = "nr_mode",
                isEnabled = true,
                isSupported = true,
                isLoading = false,
                error = null,
                configurationOptions = listOf(
                    ConfigurationOption.Choice(
                        key = "mode",
                        label = "Mode",
                        selected = "Disable SA",
                        options = listOf("Disable SA", "Disable NSA")
                    ),
                    ConfigurationOption.NumberInput(
                        key = "simSlotId",
                        label = "SIM Slot",
                        value = 0,
                        range = 0..1
                    )
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
    ApplicationTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                policyName = "nr_mode",
                isEnabled = false,
                isSupported = true,
                isLoading = false,
                error = "Failed to update policy",
                configurationOptions = listOf(
                    ConfigurationOption.Choice(
                        key = "mode",
                        label = "Mode",
                        selected = "Disable SA",
                        options = listOf("Disable SA", "Disable NSA")
                    ),
                    ConfigurationOption.NumberInput(
                        key = "simSlotId",
                        label = "SIM Slot",
                        value = 0,
                        range = 0..1
                    )
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
    ApplicationTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                policyName = "nr_mode",
                isEnabled = false,
                isSupported = false,
                isLoading = false,
                error = null,
                configurationOptions = listOf(
                    ConfigurationOption.Choice(
                        key = "mode",
                        label = "Mode",
                        selected = "Disable SA",
                        options = listOf("Disable SA", "Disable NSA")
                    ),
                    ConfigurationOption.NumberInput(
                        key = "simSlotId",
                        label = "SIM Slot",
                        value = 0,
                        range = 0..1
                    )
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
    ApplicationTheme {
        PolicyCard(
            policy = PolicyUiState.ConfigurableToggle(
                title = "5G NR Mode",
                description = "Configure 5G NR mode settings for the device",
                policyName = "nr_mode",
                isEnabled = true,
                isSupported = true,
                isLoading = true,
                error = null,
                configurationOptions = listOf(
                    ConfigurationOption.Choice(
                        key = "mode",
                        label = "Mode",
                        selected = "Disable SA",
                        options = listOf("Disable SA", "Disable NSA")
                    ),
                    ConfigurationOption.NumberInput(
                        key = "simSlotId",
                        label = "SIM Slot",
                        value = 0,
                        range = 0..1
                    )
                )
            ),
            onEvent = {},
            modifier = Modifier,
        )
    }
}