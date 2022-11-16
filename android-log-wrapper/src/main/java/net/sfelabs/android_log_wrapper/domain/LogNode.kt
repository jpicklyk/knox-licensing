package net.sfelabs.android_log_wrapper.domain

import net.sfelabs.android_log_wrapper.Priority

interface LogNode {
    fun println(priority: Priority, tag: String?, message: String, t: Throwable?)
}