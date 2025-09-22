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
            ERROR_NULL_PARAMS -> "Null parameters provided - check if license key is null or empty"
            ERROR_UNKNOWN -> "Unknown error occurred - check Knox SDK logs for more details"
            ERROR_INVALID_LICENSE -> "Invalid license key - verify the license key format and validity"
            ERROR_LICENSE_TERMINATED -> "License has been terminated - contact Samsung for license status"
            ERROR_INVALID_PACKAGE_NAME -> "Invalid package name - license not valid for this application package"
            ERROR_NOT_CURRENT_DATE -> "Device date is not current - check device time and date settings"
            ERROR_INVALID_BINDING -> "Invalid license binding - license may be bound to different device"
            ERROR_INTERNAL -> "Internal Knox error (301) - typically indicates missing Device Administrator privileges. Your app must be registered as a Device Administrator, Device Owner, or Profile Owner to use Knox licensing."
            ERROR_NETWORK_DISCONNECTED -> "Network disconnected - check internet connectivity for license validation"
            ERROR_LICENSE_DEACTIVATED -> "License has been deactivated"
            ERROR_LICENSE_EXPIRED -> "License has expired - renew the license key"
            else -> "Unknown Knox error (code: $errorCode) - check Samsung Knox documentation"
        }.also { message ->
            Log.d(tag, "Knox error code $errorCode mapped to: $message")
        }
    }
}