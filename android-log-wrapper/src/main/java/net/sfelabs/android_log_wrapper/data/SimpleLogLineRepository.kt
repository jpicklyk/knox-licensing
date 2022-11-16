package net.sfelabs.android_log_wrapper.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.android_log_wrapper.domain.repository.LogLineRepository
import java.util.LinkedList

class SimpleLogLineRepository: LogLineRepository {
    private val maxSize = 1000
    private val logBuffer = LinkedList<LogLine>()
    private val _logs = MutableSharedFlow<LogLine>(
        replay = 1,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun addLogLine(logLine: LogLine) {
        if(logBuffer.size+1 >= maxSize)
            logBuffer.removeFirst()
        logBuffer.add(logLine)
        runBlocking {
            produceEvent(logLine)
        }

    }

    override fun getLogLines(): List<LogLine> {
        return logBuffer.toList()
    }

    override fun getLatestLogLine(): LogLine? {
        return logBuffer.peekLast()
    }

    override fun clearLogs() {
        logBuffer.clear()
    }

    override fun getLogStream(): SharedFlow<LogLine> {
        return _logs.asSharedFlow()
    }


    private suspend fun produceEvent(logLine: LogLine) {
        withContext(Dispatchers.Default) {
            _logs.emit(logLine)
        }
    }
}