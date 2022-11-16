package net.sfelabs.android_log_wrapper.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import net.sfelabs.android_log_wrapper.domain.model.LogLine

interface LogLineRepository {

    fun addLogLine(logLine: LogLine)

    fun getLogLines(): List<LogLine>

    fun getLatestLogLine(): LogLine?

    fun clearLogs()

    fun getLogStream(): SharedFlow<LogLine>
}