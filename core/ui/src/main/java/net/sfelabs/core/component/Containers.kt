package net.sfelabs.core.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.core.ui.theme.AppTheme


@Composable
fun OutlinedCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier
    ) {
        // Title section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
                )
        }

        // Content
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
                //.height(IntrinsicSize.Min),
            content = content
        )

    }
}


@Preview
@Composable
fun OutlinedCardContainerPreview() {
    OutlinedCardContainer(
        title = "Title for Container",
        content = {
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                            "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                            "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                            "ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                            "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                            "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                            "mollit anim id est laborum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    )
}

@Composable
fun OutlinedCardToggleableContainer(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean = true,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = {},
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 6.dp)
                .height(intrinsicSize = IntrinsicSize.Max)
                .alpha(if (enabled) 1.0f else 0.6f)
        ) {
            // Title column
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            // Switch column
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled
                    //modifier = Modifier.weight(1f)
                )
            }

        }

        // Content
        Column(
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .alpha(if (enabled) 1.0f else 0.6f),
            //.height(IntrinsicSize.Min),
            content = content
        )

    }
}

@Preview
@Composable
fun OutlinedCardToggleableContainerPreview() {
    OutlinedCardToggleableContainer(
        title = "Toggleable Container",
        content = {
            Row {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                            "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                            "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                            "ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                            "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                            "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                            "mollit anim id est laborum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    )
}

@Preview
@Composable
fun OutlinedCardToggleableContainerDisabledPreview() {
    OutlinedCardToggleableContainer(
        title = "Disabled Container",
        enabled = false,
        content = {
            Row {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do " +
                            "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                            "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
                            "ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                            "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                            "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt " +
                            "mollit anim id est laborum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    )
}

@Composable
fun KeyValueComposable(
    modifier: Modifier = Modifier,
    key: String,
    content:  @Composable (ColumnScope.() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            content = content
        )

    }
}

@Preview
@Composable
private fun KeyValueComposableTextPreview() {
    KeyValueComposable(
        key = "Some Key: ",
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .height(20.dp)
    ) {
        Row {
            Text(
                text = "Some Value",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun KeyValueComposableIconPreview() {
    KeyValueComposable(
        key = "Some Key: "
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "",
                tint = Color.Green
            )
        }

    }
}

@Composable
fun GroupingComposable(
    groupName: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(start = 6.dp)
            )
        }
        Column(
            modifier = modifier.padding(start = 20.dp),
            content = content
        )

    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun GroupingComposablePreview() {
    AppTheme {
        OutlinedCardContainer(
            title = "Grouping Preview",
            modifier = Modifier
        ) {
            GroupingComposable(groupName = "Group Title") {
                Text(text = "Item 1", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Item 2", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

}