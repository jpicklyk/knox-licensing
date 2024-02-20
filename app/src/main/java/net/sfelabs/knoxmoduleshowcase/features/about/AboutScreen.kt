@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused"
)

package net.sfelabs.knoxmoduleshowcase.features.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.sfelabs.core.component.GroupingComposable
import net.sfelabs.core.component.OutlinedCardContainer
import net.sfelabs.core.ui.theme.AppTheme

private const val disclaimer = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed accumsan " +
        "neque vel felis luctus, condimentum dignissim felis tempus. Orci varius natoque penatibus " +
        "et magnis dis parturient montes, nascetur ridiculus mus. Vivamus libero nisl, mollis a " +
        "posuere nec, dignissim ac nisl. Nullam rhoncus, neque quis condimentum scelerisque, nulla " +
        "nisi congue nibh, sed accumsan urna lacus in nulla. Nullam eget massa consectetur, cursus " +
        "nulla quis, mollis nisl. Maecenas tempor, turpis non rhoncus pharetra, lorem leo dictum " +
        "erat, et consequat lectus enim a quam. Etiam vulputate justo in erat hendrerit, non " +
        "vestibulum enim tempor.\n" +
        "\n" +
        "Sed consectetur eleifend risus, vel egestas mi ultricies non. Nulla vitae sodales lacus. " +
        "Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris dignissim diam at " +
        "erat tincidunt aliquam. Aenean condimentum hendrerit purus, nec placerat nibh lobortis " +
        "accumsan. Nulla et ligula eget magna lacinia auctor non in quam. Aenean facilisis " +
        "scelerisque nunc, at congue lorem tempor sit amet. Proin ac bibendum leo. In hendrerit " +
        "scelerisque mi.\n"

@Composable fun AboutScreen(
    state: InformationState,
    isLoaded: Boolean = state.isLoaded
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if(isLoaded) {
            OutlinedCardContainer(title = "Tactical Edition Gen2 Software Versions") {
                GroupingComposable(groupName = "Included Software") {
                    state.gen2SoftwareList.forEach { build ->
                        Text(
                            text = "${build.buildNumber} (${build.versionName})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
                GroupingComposable(groupName = "Extension Program") {
                    state.gen2ExtensionSoftwareList.forEach { build ->
                        Text(
                            text = "${build.buildNumber} (${build.versionName})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            OutlinedCardContainer(title = "Tactical Edition Gen3 Software Versions") {
                GroupingComposable(groupName = "Included Software") {
                    state.gen3SoftwareList.forEach { build ->
                        Text(
                            text = "${build.buildNumber} (${build.versionName})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewAboutScreen() {
    AppTheme {
        AboutScreen(InformationState())
    }

}

