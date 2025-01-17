package com.example.knox.feature.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private fun getOptionsForKey(key: String): List<String> = when {
    key.equals("mode", ignoreCase = true) -> listOf("Both SA and NSA", "NSA Only", "SA Only")  // For NR mode
    else -> emptyList()
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    isEnabled: Boolean,
    isSupported: Boolean,
    configurationOptions: Map<String, Any?>? = null,
    onToggle: (Boolean) -> Unit,
    onConfigChange: ((String, Any) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        enabled = isSupported,
        onClick = { expanded = !expanded }
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
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
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (!isSupported) {
                Text(
                    text = "(This feature is not supported on this device)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Basic content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
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

            // Configuration section
            if (expanded && configurationOptions != null && isEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Divider
                    androidx.compose.material3.Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Configuration options
                    configurationOptions.forEach { (key, value) ->
                        when (value) {
                            is Boolean -> ConfigurationSwitch(
                                label = key,  // Utility function to format the key
                                checked = value,
                                onCheckedChange = { onConfigChange?.invoke(key, it) }
                            )
                            is String -> when {
                                key.equals("mode", ignoreCase = true) -> ConfigurationDropdown(
                                    label = "Mode",
                                    selected = value,
                                    options = getOptionsForKey(key),
                                    onSelectionChange = { onConfigChange?.invoke(key, it) }
                                )
                                else -> ConfigurationText(
                                    label = key,
                                    value = value,
                                    onValueChange = { onConfigChange?.invoke(key, it) }
                                )
                            }
                            is Int -> {
                                if (key.equals("band", ignoreCase = true)) {
                                    ConfigurationNumber(
                                        label = "Band",
                                        value = value.coerceAtLeast(0),  // Prevent negative values
                                        onValueChange = { onConfigChange?.invoke(key, it) }
                                    )
                                }
                                if (key.equals("simSlotId", ignoreCase = true)) {
                                    ConfigurationNumber(
                                        label = "SIM Slot",
                                        value = value,
                                        onValueChange = { onConfigChange?.invoke(key, it.coerceIn(0, 1)) }
                                    )
                                }
                            }
                        }
                    }
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
                    enabled = true,
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
private fun ConfigurationText(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ConfigurationNumber(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {
                it.toIntOrNull()?.let { num ->
                    val coercedValue = when(label) {
                        "SIM Slot" -> num.coerceIn(0, 1)
                        else -> num.coerceAtLeast(0)
                    }
                    onValueChange(coercedValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}