package net.sfelabs.android_log_wrapper.domain.model

import net.sfelabs.android_log_wrapper.Priority

data class LogLine(
    val priority: Priority,
    val tag: String = "",
    val message: String,
    val throwable: Throwable? = null
)
