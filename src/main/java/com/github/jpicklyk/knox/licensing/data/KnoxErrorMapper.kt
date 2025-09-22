package com.github.jpicklyk.knox.licensing.data

import android.util.Log
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INTERNAL
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_BINDING
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_DEACTIVATED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_EXPIRED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NONE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_UNKNOWN
internal class KnoxErrorMapper {
    private val tag = "KnoxErrorMapper"

    fun getKnoxErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            ERROR_NONE -> "No error"
            ERROR_NULL_PARAMS -> "Null parameters provided"
            ERROR_UNKNOWN -> "Unknown error occurred"
            ERROR_INVALID_LICENSE -> "Invalid license key"
            ERROR_LICENSE_TERMINATED -> "License has been terminated"
            ERROR_INVALID_PACKAGE_NAME -> "Invalid package name"
            ERROR_NOT_CURRENT_DATE -> "Device date is not current"
            ERROR_INVALID_BINDING -> "Invalid license binding"
            ERROR_INTERNAL -> "Internal Knox error"
            ERROR_NETWORK_DISCONNECTED -> "Network disconnected"
            ERROR_LICENSE_DEACTIVATED -> "License has been deactivated"
            ERROR_LICENSE_EXPIRED -> "License has expired"
            else -> "Unknown error (code: $errorCode)"
        }.also { message ->
            Log.d(tag, "Knox error code $errorCode: $message")
        }
    }
}