package net.sfelabs.android_log_wrapper.domain

import android.util.Log
import net.sfelabs.android_log_wrapper.Priority

/**
 * Helper class that allows chaining together multiple LogNodes to extend the capability of the
 * default Android logger
 */

class LogWrapper(
    val nextNode: LogNode? = null
    ): LogNode{
    var tag = "LogWrapper"
    var packageName = ""
    var className = ""
    private var isLoggingEnabled = true
    private var logLevelEnabledMap: Map<Priority, Boolean> = mapOf(
        Priority.VERBOSE to true, Priority.DEBUG to true, Priority.INFO to true,
        Priority.WARN to true, Priority.ERROR to true, Priority.ASSERT to true
    )
    var isClassNameVisible = true
    var isPackageNameVisible = false
    var isTimeVisible = true
    var timeFormat = "HH:mm:ss:SSS"

    override fun println(priority: Priority, tag: String?, message: String, t: Throwable?) {
        if(!isLoggingEnabled || !logLevelEnabledMap[priority]!!) return

        if(t != null) {
            message + "\n" + Log.getStackTraceString(t)
        }

        val myTag = null//if(customTag == null) tag else "$tag>$customTag"

        when(priority) {
            is Priority.VERBOSE -> Log.v(myTag,message, t)
            is Priority.DEBUG -> Log.d(myTag,message, t)
            is Priority.INFO -> Log.i(myTag,message, t)
            is Priority.WARN -> Log.w(myTag,message, t)
            is Priority.ERROR -> Log.e(myTag,message, t)
            is Priority.ASSERT -> Log.wtf(myTag,message, t)
        }

        nextNode?.println(priority, myTag, message, t)
    }
}

