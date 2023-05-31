package net.sfelabs.common.core

import android.os.Build
import net.sfelabs.common.core.ui.UiText
import java.lang.reflect.Method

typealias UnitResource = Resource<Unit>
typealias UnitApiCall = ApiCall<Unit>

/**
 * Utility package for various Android helper functions.
 */
sealed class ApiCall<out T : Any> {
    data class Success<out T : Any>(val data: T, val uiText: UiText? = null): ApiCall<T>()
    data class Error(val uiText: UiText): ApiCall<Nothing>()
    object NotSupported: ApiCall<Nothing>()
}

sealed class Resource<T>(val data: T? = null, val uiText: UiText? = null) {
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Success<T>(data: T? = null): Resource<T>(data)
    class Error<T>(data: T? = null, uiText: UiText): Resource<T>(data, uiText)
}

fun getCallerClassName(clazz: Class<*>): String? {
    val stackTrace = Thread.currentThread().stackTrace
    val className = clazz.name
    var classFound = false
    for (i in 1 until stackTrace.size) {
        val element = stackTrace[i]
        val callerClassName = element.className
        // check if class name is the requested class
        if (callerClassName == className) classFound =
            true else if (classFound) return callerClassName
    }
    return null
}

/**
 * Android removed the ability to pull IMEI and Serial numbers from the device unless
 * the application is a system app or the DPC retrieves it during the managed provisioning process.
 * The following reflection code will work on Android 10 or below but has be since patched.
 */

fun getDeviceSerialNumber(): String? {
    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
        return null
        /*throw UnsupportedOperationException(
            "Android no longer supports the ability to retrieve " +
                    "the device Serial number or IMEI"
        )
         */
    }
    val c = Class.forName("android.os.SystemProperties")
    val get: Method = c.getMethod("get", String::class.java)
    return get.invoke(c, "ril.serialnumber") as String

}