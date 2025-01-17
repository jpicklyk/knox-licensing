package net.sfelabs.knoxmoduleshowcase.features.policy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.knox.feature.compose.screen.FeatureLazyList
import androidx.compose.runtime.getValue

@Composable
fun PolicyScreen() {
    val viewModel: PoliciesViewModel = hiltViewModel()
    val features by viewModel.features.collectAsState()
    FeatureLazyList(
        features = features,
        onToggle = viewModel::toggleFeature,
        onConfigChange = viewModel::updateFeatureConfig
    )
}