package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.knoxmoduleshowcase.features.policy.components.CollapsibleGroupHeader
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyCard
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicySearchBar
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyTabRow
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent

@Composable
fun PolicyScreen(
    viewModel: PoliciesViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky header with search and tabs
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column {
                // Search bar
                PolicySearchBar(
                    query = screenState.searchQuery,
                    onQueryChange = { viewModel.onEvent(PolicyEvent.UpdateSearchQuery(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Tab row (shows filtered counts when searching)
                PolicyTabRow(
                    selectedTab = screenState.selectedTab,
                    tacticalCount = screenState.filteredTacticalPolicyCount,
                    enterpriseCount = screenState.filteredEnterprisePolicyCount,
                    onTabSelected = { viewModel.onEvent(PolicyEvent.SelectTab(it)) }
                )
            }
        }

        // Policy list
        val filteredGroups = screenState.filteredGroups()

        when {
            screenState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredGroups.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (screenState.searchQuery.isNotEmpty())
                            "No policies match your search"
                        else
                            "No policies available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredGroups.forEach { group ->
                        // Group header (collapsible)
                        item(key = "header_${group.groupId}") {
                            CollapsibleGroupHeader(
                                group = group,
                                onToggleExpand = {
                                    viewModel.onEvent(PolicyEvent.ToggleGroupExpansion(group.groupId))
                                }
                            )
                        }

                        // Policies in group (only rendered when expanded)
                        if (group.isExpanded) {
                            items(
                                items = group.policies,
                                key = { policy -> "${group.groupId}_${policy.policyName}" }
                            ) { policy ->
                                PolicyCard(
                                    policy = policy,
                                    onEvent = viewModel::onEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
