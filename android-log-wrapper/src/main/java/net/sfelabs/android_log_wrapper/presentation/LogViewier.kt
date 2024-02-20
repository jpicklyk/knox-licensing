package net.sfelabs.android_log_wrapper.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.sfelabs.android_log_wrapper.Priority
import net.sfelabs.android_log_wrapper.domain.model.LogLine

@Composable
fun LogViewer(
    modifier: Modifier = Modifier,
    viewModel: LogViewerViewModel = hiltViewModel(),
    backgroundColor: Color = Color.Black
) {
    val state = viewModel.state
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    modifier.defaultMinSize(minHeight = 60.dp)
    BoxWithConstraints(modifier = modifier
    ) {
        val boxWithConstraintsScope = this
        LazyColumn(
            modifier = Modifier
                .defaultMinSize(minHeight = 20.dp)
                .fillMaxSize(1F)
                .background(backgroundColor)
                .padding(bottom = 8.dp)
                .horizontalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            state = listState
        ) {
            items(state.value.logLines) { line ->
                LogRow(logLine = line)
            }
        }
    }
    LaunchedEffect(listState.layoutInfo.totalItemsCount) {
        listState.scrollToItem(listState.layoutInfo.totalItemsCount)
    }

}

internal data class LogSymbolFormat(
    val text: String, val textColor: Color, val backgroundColor: Color
)

@Composable
fun LogRow(logLine: LogLine) {

    Row {
        Column(
            modifier = Modifier
                .width(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logSymbol: LogSymbolFormat = when(logLine.priority) {
                is Priority.VERBOSE -> LogSymbolFormat("V", Color.Black, Color.White)
                is Priority.DEBUG -> LogSymbolFormat("D", Color.White, Color.Green)
                is Priority.INFO -> LogSymbolFormat("I", Color.LightGray, Color.Blue)
                is Priority.WARN -> LogSymbolFormat("W", Color.Black, Color.Yellow)
                is Priority.ERROR -> LogSymbolFormat("E", Color.Black, Color.Red)
                is Priority.ASSERT -> LogSymbolFormat("!", Color.Black, Color.Cyan)
            }
            Text(
                text = logSymbol.text,
                color = logSymbol.textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = logSymbol.backgroundColor)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            val logColor: Color =
                when(logLine.priority) {
                    is Priority.WARN -> Color.Yellow
                    is Priority.ERROR -> Color.Red
                    is Priority.ASSERT -> Color.Red
                    else -> Color.White
                }
            Text(
                text = logLine.message,
                color = logColor
            )
        }
    }


}

