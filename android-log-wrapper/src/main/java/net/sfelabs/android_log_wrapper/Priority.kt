package net.sfelabs.android_log_wrapper

/**
 *
public static final int ASSERT = 7;
public static final int DEBUG = 3;
public static final int ERROR = 6;
public static final int INFO = 4;
public static final int VERBOSE = 2;
public static final int WARN = 5;

 */
sealed class Priority(val value: Int) {
    object VERBOSE: Priority(value = 2)
    object DEBUG: Priority(value = 3)
    object INFO: Priority(value = 4)
    object WARN: Priority(value = 5)
    object ERROR: Priority(value = 6)
    object ASSERT: Priority(value = 7)

    fun Int.toPriority(): Priority =
        when(this) {
            2 -> VERBOSE
            3 -> DEBUG
            4 -> INFO
            5 -> WARN
            6 -> ERROR
            else -> ASSERT

    }
}
