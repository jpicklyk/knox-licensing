package net.sfelabs.knoxmoduleshowcase.features.tactical.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import net.sfelabs.common.knox.KnoxApi
import net.sfelabs.common.knox.KnoxComponentType
import kotlin.math.roundToInt

fun Modifier.maxWidth(
    fraction: Float = 1f,
) = layout { measurable, constraints ->
    val maxWidth = (constraints.maxWidth * fraction).roundToInt()
    val width = measurable.maxIntrinsicWidth(constraints.maxHeight).coerceAtMost(maxWidth)

    val placeable = measurable.measure(Constraints(constraints.minWidth, width, constraints.minHeight, constraints.maxHeight))
    layout(width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnoxApiComponent(
    title: String,
    description: String,
    onChanged: ((Boolean) -> Unit),
    isFeatureSupported: Boolean = true,
    expanded: Boolean = false,
    componentType: KnoxComponentType

    ) {
    var expandedState by remember {
        mutableStateOf(expanded)
    }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        enabled = isFeatureSupported,
        onClick = { expandedState = !expandedState }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                if(expandedState) {
                    Icon(imageVector = Icons.Filled.ExpandLess, null, tint = Color.White)
                } else {
                    Icon(imageVector = Icons.Filled.ExpandMore, null, tint = Color.White)
                }
            }


        }
        if(!isFeatureSupported) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "(This Knox API is not supported on this device)",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            if(expandedState && description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 8.dp, bottom = 8.dp)
                        .weight(1f, fill = false)
                )

            } else {
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 12.dp, bottom = 8.dp)
                        .weight(1f, fill = false)
                )
            }

            when(componentType) {
                is KnoxComponentType.BooleanComponent ->
                    KnoxBooleanApiComponent(
                        checked = componentType.checkedState,
                        enabled = isFeatureSupported,
                        onSwitchChanged = onChanged
                    )
                is KnoxComponentType.SpinnerComponent ->
                    KnoxApiTextFieldComponent(
                        expandedState = expandedState,
                        checked = componentType.checkedState,
                        onSwitchChanged = onChanged
                    )

            }
        }
    }

}


@Composable
fun KnoxBooleanApiComponent(
    checked: Boolean?,
    enabled: Boolean = true,
    onSwitchChanged: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(end = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {

        if (checked != null) {
            Switch(
                checked = checked,
                onCheckedChange = onSwitchChanged,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnoxApiTextFieldComponent(
    expandedState: Boolean,
    checked: Boolean?,
    onSwitchChanged: (Boolean) -> Unit
) {
    var band: String by remember { mutableStateOf("0")}

        Column(
            modifier = Modifier
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = band,
                    onValueChange = { band = it },
                    label = { Text(text = "Band")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .width(70.dp)
                        .padding(end = 8.dp)
                        //.weight(1f, false)
                )
                if(checked != null) {
                    Switch(
                        checked = checked,
                        onCheckedChange = onSwitchChanged,
                        //modifier = Modifier.weight(1f)
                    )
                }
            }
        }
}


val knoxApi = KnoxApi(
    name = "Tactical Device Mode",
    description = "Tactical Device Mode disables all cellular communication including Emergency " +
            "911 services.  The device user will not be able to turn off Airplane Mode and only" +
            " wired communication will be allowed.",
    knoxComponentType = KnoxComponentType.BooleanComponent(
        false
    ),
    callingClass = "com.samsung.android.knox.restriction.RestrictionPolicy",
    functionName = "isTacticalDeviceModeEnabled",
    onChanged = { }
)

@Preview
@Composable
fun PreviewSwitchComponent() {
    KnoxApiComponent(
        title = knoxApi.name,
        description = knoxApi.description?:"",
        isFeatureSupported = true,
        onChanged = knoxApi.onChanged,
        expanded = false,
        componentType = knoxApi.knoxComponentType
    )
}

@Preview
@Composable
fun PreviewSwitchComponentExpanded() {
    KnoxApiComponent(
        title = knoxApi.name,
        description = knoxApi.description?:"",
        isFeatureSupported = true,
        onChanged = knoxApi.onChanged,
        expanded = true,
        componentType = knoxApi.knoxComponentType
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSpinnerComponent() {
    KnoxApiComponent(
        title = "LTE Band Lock",
        description = "Lock the LTE frequency band to that specified osiadgjolkad;g osaifgosdfjo " +
                "sjdfj jso jdifj jdsj ojs; jfo j",
        isFeatureSupported = true,
        expanded = false,
        componentType = KnoxComponentType.SpinnerComponent(checkedState = false),
        onChanged = {}
    )
}