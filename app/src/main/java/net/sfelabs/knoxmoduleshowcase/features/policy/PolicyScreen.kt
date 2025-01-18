package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState
import net.sfelabs.knoxmoduleshowcase.features.policy.components.PolicyCard

@Composable
fun PolicyScreen(
    viewModel: PoliciesViewModel = hiltViewModel()
) {
    val policies by viewModel.policies.collectAsState()
    PolicyList(
        policies = policies,
        onToggle = viewModel::toggleFeature,
        onConfigChange = viewModel::updateFeatureConfig
    )
}

@Composable
private fun PolicyList(
    policies: List<PolicyUiState>,
    onToggle: (String, Boolean) -> Unit,
    onConfigChange: (String, String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(policies) { policy ->
            when (policy) {
                is PolicyUiState.Toggle -> PolicyCard(
                    policy = policy,
                    onToggle = { enabled -> onToggle(policy.featureName, enabled) }
                )
                is PolicyUiState.ConfigurableToggle -> PolicyCard(
                    policy = policy,
                    onToggle = { enabled -> onToggle(policy.featureName, enabled) },
                    onConfigChange = { key, value ->
                        onConfigChange(policy.featureName, key, value)
                    }
                )
            }
        }
    }
}