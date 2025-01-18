package net.sfelabs.knoxmoduleshowcase.features.policy.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.sfelabs.core.knox.feature.ui.model.PolicyUiState

@Composable
fun PolicyCard(
    policy: PolicyUiState,
    onToggle: (Boolean) -> Unit,
    onConfigChange: ((String, Any) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        enabled = policy.isSupported,
        onClick = { /* No click handler needed */ }
    ) {
        Column {
            PolicyCardHeader(
                title = policy.title,
                expanded = false  // No expansion needed
            )

            if (!policy.isSupported) {
                PolicySupportStatus()
            }

            policy.error?.let { error ->
                PolicyErrorMessage(error)
            }

            if (policy.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            PolicyCardContent(
                description = policy.description,
                isEnabled = policy.isEnabled,
                isSupported = policy.isSupported,
                onToggle = onToggle
            )

            if (policy is PolicyUiState.ConfigurableToggle && policy.isSupported) {
                PolicyConfiguration(
                    options = policy.configurationOptions,
                    onConfigChange = onConfigChange
                )
            }
        }
    }
}