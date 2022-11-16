package net.sfelabs.knoxmoduleshowcase.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.android_log_wrapper.presentation.LogViewer
import net.sfelabs.knoxmoduleshowcase.presentation.screens.logviewer.LogViewerViewModel

@Composable
fun SampleLogViewerScreen(
    viewModel: LogViewerViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Sample Log Viewer Screen")

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { viewModel.generateLogLine() }
        ) {
            Text(text = "Generate Log")
        }
        LogViewer()
    }
    
}
