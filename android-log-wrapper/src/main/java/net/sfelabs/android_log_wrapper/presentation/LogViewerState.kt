package net.sfelabs.android_log_wrapper.presentation

import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.common.core.ui.UiText

data class LogViewerState(
    //TODO - Handle loading use case
    val isLoading: Boolean = false,
    val logLines: List<LogLine> = emptyList(),
    //TODO - Handle error use case
    val error: UiText? = null
)
