package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components

import android.content.res.Configuration
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfigurationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressTypeSelector(
    selectedAddressType: InterfaceAddressConfigurationType,
    onAddressTypeChanged: (InterfaceAddressConfigurationType) -> Unit
) {
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    //var text = selectedConfigurationType.toString()
    val options = InterfaceAddressConfigurationType.allTypes

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = selectedAddressType.toString(),
            label = { Text(text = "IP Assignment") },
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false}
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option.value, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        //text = option.value
                        isExpanded = false
                        onAddressTypeChanged(option)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun InterfaceConfigurationTypeSelectorPreview() {
    AddressTypeSelector(
        selectedAddressType = InterfaceAddressConfigurationType.DHCP,
        onAddressTypeChanged = { }
    )
}