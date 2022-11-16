package net.sfelabs.android_log_wrapper.domain

import android.util.Log
import net.sfelabs.android_log_wrapper.Priority
import net.sfelabs.android_log_wrapper.domain.model.LogLine
import net.sfelabs.android_log_wrapper.domain.repository.LogLineRepository
import javax.inject.Inject


class LogToRepositoryNode @Inject constructor(
    private val repository: LogLineRepository,
    val nextNode: LogNode? = null
): LogNode {

     override fun println(priority: Priority, tag: String?, message: String, t: Throwable?) {
        if(t != null) {
            message + "\n" + Log.getStackTraceString(t)
        }
        repository.addLogLine(LogLine(priority, tag ?: "", message))
        nextNode?.println(priority, tag, message, t)
    }

}