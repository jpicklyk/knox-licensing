package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyCard

@Composable
fun PolicyScreen(
    viewModel: PoliciesViewModel = hiltViewModel()
) {
    val policies by viewModel.policies.collectAsState()
    LazyColumn {
        items(
            items = policies,
            key = { policy -> policy.featureName }
        ) { policy ->
            PolicyCard(
                policy = policy,
                onEvent = viewModel::onEvent,
            )
        }
    }
}
