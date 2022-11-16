package net.sfelabs.android_log_wrapper.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sfelabs.android_log_wrapper.Priority
import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.android_log_wrapper.domain.use_case.LogViewerUseCase
import net.sfelabs.android_log_wrapper.domain.use_case.StreamLogLinesUseCase
import net.sfelabs.common.core.Resource
import javax.inject.Inject

@HiltViewModel
class LogViewerViewModel @Inject constructor(
    private val getLogLinesUseCase: LogViewerUseCase,
    private val getStreamingLogLinesUseCase: StreamLogLinesUseCase
): ViewModel() {

    private val _state = MutableStateFlow(LogViewerState())
    val state: StateFlow<LogViewerState> = _state

    init {
        getLogLines()
        streamLogLines()
    }

    private fun getLogLines() {
        viewModelScope.launch(Dispatchers.IO) {
            getLogLinesUseCase().collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _state.update {it.copy(logLines = (result.data ?: emptyList<List<LogLine>>()) as List<LogLine>)}
                    }
                    is Resource.Error -> {
                        _state.update{it.copy(error = result.uiText)}
                    }
                    is Resource.Loading -> {
                        val l = listOf(LogLine(Priority.INFO,"","Loading..."))
                        _state.update{ it.copy(isLoading = true, logLines = l)}
                    }
                }
            }
        }
    }

    private fun streamLogLines() {
        viewModelScope.launch(Dispatchers.IO) {
            getStreamingLogLinesUseCase()
                .catch { exception -> exception.printStackTrace() }
                .collect { result ->
                val current: List<LogLine> = _state.value.logLines
                _state.update { it.copy(logLines = current.plus(result))}
            }
        }
    }
}
