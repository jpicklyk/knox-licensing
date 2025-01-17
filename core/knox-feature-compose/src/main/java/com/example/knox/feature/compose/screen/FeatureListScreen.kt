package com.example.knox.feature.compose.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.knox.feature.compose.components.FeatureCard
import net.sfelabs.core.knox.feature.ui.model.FeatureUiState

@Composable
fun FeatureLazyList(
    features: List<FeatureUiState>,
    onToggle: (String, Boolean) -> Unit,
    onConfigChange: (String, String, Any) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(features) { feature ->
            when (feature) {
                is FeatureUiState.Toggle -> FeatureCard(
                    title = feature.title,
                    description = feature.description,
                    isEnabled = feature.isEnabled,
                    isSupported = feature.error == null,
                    onToggle = { enabled -> onToggle(feature.featureName, enabled) }
                )
                is FeatureUiState.ConfigurableToggle -> FeatureCard(
                    title = feature.title,
                    description = feature.description,
                    isEnabled = feature.isEnabled,
                    isSupported = feature.error == null,
                    configurationOptions = feature.configurationOptions,
                    onToggle = { enabled -> onToggle(feature.featureName, enabled) },
                    onConfigChange = { key, value ->
                        onConfigChange(feature.featureName, key, value)
                    }
                )
            }
        }
    }
}