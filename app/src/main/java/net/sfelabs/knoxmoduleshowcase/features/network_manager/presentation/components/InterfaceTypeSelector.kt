package net.sfelabs.knoxmoduleshowcase.features.network_manager.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterfaceTypeSelector(
    selectedInterfaceType: InterfaceType,
    onInterfaceTypeSelected: (InterfaceType) -> Unit = {},
    readOnly: Boolean = false
) {
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    val options = InterfaceType.allTypes

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { if(!readOnly)isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            value = selectedInterfaceType.value,
            label = { Text(text = "Interface Type") },
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                if(!readOnly)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                           },
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
                        onInterfaceTypeSelected(option)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}