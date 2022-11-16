package net.sfelabs.android_log_wrapper

import net.sfelabs.android_log_wrapper.domain.LogNode
import net.sfelabs.common.core.getCallerClassName
import javax.inject.Inject

/**
 * Drop in class for Android Util Logging.
 * I feel that this needs to be reworked a bit and find a way to remove the injection so I can
 * expose the logging functions statically.
 * TODO: Refactor code to remove DI and expose functions statically
 */
class Log @Inject constructor(
    private val nextNode: LogNode
) {
    fun println(priority: Priority, tag: String? = getCallerClassName(Log::class.java), message: String, throwable: Throwable? = null) {
        nextNode.println(priority, tag, message, throwable)
    }

    fun v(message: String, throwable: Throwable? = null) {
        println(Priority.VERBOSE, message = message, throwable = throwable)
    }

    fun d(message: String, throwable: Throwable? = null) {
        println(Priority.DEBUG, message = message, throwable = throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        println(Priority.ERROR, message = message, throwable = throwable)
    }

    fun w(message: String, throwable: Throwable? = null) {
        println(Priority.WARN, message = message, throwable = throwable)
    }

    fun i(message: String) {
        println(Priority.INFO, message = message)
    }

    fun wtf(message: String, throwable: Throwable? = null) {
        println(Priority.ASSERT, message = message, throwable = throwable)
    }

}
