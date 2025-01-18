package net.sfelabs.knoxmoduleshowcase.features.policy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.sfelabs.knox_tactical.domain.model.AutoCallPickupMode
import net.sfelabs.knox_tactical.domain.model.LteNrMode

@Composable
internal fun PolicyCardHeader(
    title: String,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
internal fun PolicySupportStatus(
    modifier: Modifier = Modifier
) {
    Text(
        text = "(This feature is not supported on this device)",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
internal fun PolicyErrorMessage(
    error: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
internal fun PolicyCardContent(
    description: String,
    isEnabled: Boolean,
    isSupported: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            enabled = isSupported,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
internal fun PolicyConfiguration(
    options: Map<String, Any?>,
    onConfigChange: ((String, Any) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        options.forEach { (key, value) ->
            when (value) {
                is Boolean -> ConfigurationCheckbox(
                    label = key,
                    checked = value,
                    onCheckedChange = { onConfigChange?.invoke(key, it) }
                )
                is String -> ConfigurationDropdown(
                    label = key,
                    selected = value,
                    options = when {
                        key.equals("mode", ignoreCase = true) ->
                            listOf("Both SA and NSA", "NSA Only", "SA Only")
                        else -> emptyList()
                    },
                    onSelectionChange = { onConfigChange?.invoke(key, it) }
                )
                is LteNrMode -> ConfigurationDropdown(
                    label = key,
                    selected = when (value) {
                        LteNrMode.EnableBothSaAndNsa -> "Enable Both SA and NSA"
                        LteNrMode.DisableSa -> "Disable SA"
                        LteNrMode.DisableNsa -> "Disable NSA"
                    },
                    options = listOf(
                        "Enable Both SA and NSA",
                        "Disable SA",
                        "Disable NSA"
                    ),
                    onSelectionChange = { selected ->
                        val mode = when (selected) {
                            "Enable Both SA and NSA" -> LteNrMode.EnableBothSaAndNsa
                            "Disable SA" -> LteNrMode.DisableSa
                            "Disable NSA" -> LteNrMode.DisableNsa
                            else -> return@ConfigurationDropdown
                        }
                        onConfigChange?.invoke(key, mode)
                    }
                )
                is AutoCallPickupMode -> {
                    println("Handling AutoCallPickupMode: $value")
                    ConfigurationDropdown(
                        label = key,
                        selected = when (value) {
                            AutoCallPickupMode.Enable -> "Enable"
                            AutoCallPickupMode.EnableAlwaysAccept -> "Enable Always Accept"
                            else -> "Enable Always Accept"  // Default case
                        },
                        options = listOf(
                            "Enable",
                            "Enable Always Accept"
                        ),
                        onSelectionChange = { selected ->
                            val mode = when (selected) {
                                "Enable" -> AutoCallPickupMode.Enable
                                "Enable Always Accept" -> AutoCallPickupMode.EnableAlwaysAccept
                                else -> return@ConfigurationDropdown
                            }
                            println("AutoCallPickupMode mapped to: $mode")
                            onConfigChange?.invoke(key, mode)
                        }
                    )
                }
                is Int -> when {
                    key.equals("band", ignoreCase = true) -> ConfigurationNumber(
                        label = "Band",
                        value = value,
                        onValueChange = { onConfigChange?.invoke(key, it) }
                    )
                    key.equals("simSlotId", ignoreCase = true) -> ConfigurationNumber(
                        label = "SIM Slot",
                        value = value,
                        onValueChange = { onConfigChange?.invoke(key, it) }
                    )
                    key.equals("feature", ignoreCase = true) -> ConfigurationNumber(
                        label = "Feature",
                        value = value,
                        onValueChange = { onConfigChange?.invoke(key, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurationNumber(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember { mutableStateOf(value.toString()) }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                // Allow empty or numeric input
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    textValue = newValue

                    // Only call onValueChange if there's a valid non-empty integer
                    newValue.toIntOrNull()?.let {
                        onValueChange(it)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigurationDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectionChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurationSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ConfigurationRadioGroup(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RadioButton(
                selected = checked,
                onClick = { onCheckedChange(true) },
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "Yes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            RadioButton(
                selected = !checked,
                onClick = { onCheckedChange(false) },
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "No",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ConfigurationCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}