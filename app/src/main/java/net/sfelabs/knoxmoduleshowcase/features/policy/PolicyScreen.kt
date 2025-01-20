package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyCard
import net.sfelabs.knoxmoduleshowcase.features.policy.event.PolicyEvent

@Composable
fun PolicyScreen(
    viewModel: PoliciesViewModel = hiltViewModel()
) {
    val policies by viewModel.policies.collectAsState()
    LazyColumn {
        items(policies) { policy ->
            PolicyCard(
                policy = policy,
                onEvent = viewModel::onEvent,
            )
        }
    }
}

@Composable
private fun PolicyList(
    policies: List<PolicyUiState>,
    onEvent: (PolicyEvent, String) -> Unit,
    modifier: Modifier = Modifier
) {

}