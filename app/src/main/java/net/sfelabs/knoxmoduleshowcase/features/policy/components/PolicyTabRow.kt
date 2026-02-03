package net.sfelabs.knoxmoduleshowcase.features.policy.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.sfelabs.knoxmoduleshowcase.features.policy.model.SdkSource

@Composable
fun PolicyTabRow(
    selectedTab: SdkSource,
    tacticalCount: Int,
    enterpriseCount: Int,
    onTabSelected: (SdkSource) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = SdkSource.entries.indexOf(selectedTab),
        modifier = modifier
    ) {
        SdkSource.entries.forEach { tab ->
            val count = when (tab) {
                SdkSource.TACTICAL -> tacticalCount
                SdkSource.ENTERPRISE -> enterpriseCount
            }
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text("${tab.displayName} ($count)")
                }
            )
        }
    }
}
