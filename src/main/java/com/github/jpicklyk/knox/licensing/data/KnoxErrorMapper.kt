package com.github.jpicklyk.knox.licensing.data

import android.util.Log
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_ANOTHER_PROCESS_IN_PLACE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INTERNAL
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_BINDING
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_ACTIVATION_NOT_FOUND
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_DEACTIVATED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_EXPIRED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NONE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_UNKNOWN
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT
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
            ERROR_INTERNAL_SERVER -> "Internal server error"
            ERROR_NETWORK_DISCONNECTED -> "Network disconnected"
            ERROR_NETWORK_GENERAL -> "Network error"
            ERROR_USER_DISAGREES_LICENSE_AGREEMENT -> "User disagreed to license agreement"
            ERROR_ANOTHER_PROCESS_IN_PLACE -> "Another license process is in progress"
            ERROR_LICENSE_DEACTIVATED -> "License has been deactivated"
            ERROR_LICENSE_EXPIRED -> "License has expired"
            ERROR_LICENSE_QUANTITY_EXHAUSTED -> "License quantity exhausted"
            ERROR_LICENSE_ACTIVATION_NOT_FOUND -> "License activation not found"
            ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE -> "License quantity exhausted on auto-release"
            else -> "Unknown error (code: $errorCode)"
        }.also { message ->
            Log.d(tag, "Knox error code $errorCode: $message")
        }
    }
}