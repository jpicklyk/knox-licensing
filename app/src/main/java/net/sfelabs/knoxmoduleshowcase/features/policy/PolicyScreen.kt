package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyCard
import net.sfelabs.knoxmoduleshowcase.features.policy.model.PolicyGroupUiState

@Composable
fun PolicyScreen(
    viewModel: PoliciesViewModel = hiltViewModel()
) {
    val groupedPolicies by viewModel.groupedPolicies.collectAsState()

    LazyColumn {
        groupedPolicies.forEach { group ->
            // Group header
            item(key = "header_${group.groupId}") {
                PolicyGroupHeader(group = group)
            }

            // Policies in group
            items(
                items = group.policies,
                key = { policy -> "${group.groupId}_${policy.policyName}" }
            ) { policy ->
                PolicyCard(
                    policy = policy,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun PolicyGroupHeader(group: PolicyGroupUiState) {
    Text(
        text = group.groupName,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}
