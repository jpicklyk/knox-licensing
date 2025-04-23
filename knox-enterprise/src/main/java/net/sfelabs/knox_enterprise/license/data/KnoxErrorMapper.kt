package net.sfelabs.knox_enterprise.license.data

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
import net.sfelabs.knox_enterprise.R
import net.sfelabs.knox_enterprise.api.ResourceProvider
import java.util.MissingFormatArgumentException
import javax.inject.Inject


class KnoxErrorMapper @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    private val tag = "KnoxErrorMapper"
    fun getKpeErrorMessage(errorCode: Int): String {
        val resourceId = when (errorCode) {
            ERROR_NONE -> R.string.knox_standard_err_kpe_none
            ERROR_NULL_PARAMS -> R.string.knox_standard_err_kpe_null_params
            ERROR_UNKNOWN -> R.string.knox_standard_err_kpe_unknown
            ERROR_INVALID_LICENSE -> R.string.knox_standard_err_kpe_licence_invalid_license
            ERROR_LICENSE_TERMINATED -> R.string.knox_standard_err_kpe_licence_terminated
            ERROR_INVALID_PACKAGE_NAME -> R.string.knox_standard_err_kpe_invalid_package_name
            ERROR_NOT_CURRENT_DATE -> R.string.knox_standard_err_kpe_not_current_date
            ERROR_INVALID_BINDING -> R.string.knox_standard_err_kpe_invalid_binding
            ERROR_INTERNAL -> R.string.knox_standard_err_kpe_internal
            ERROR_INTERNAL_SERVER -> R.string.knox_standard_err_kpe_internal_server
            ERROR_NETWORK_DISCONNECTED -> R.string.knox_standard_err_kpe_network_disconnected
            ERROR_NETWORK_GENERAL -> R.string.knox_standard_err_kpe_network_general
            ERROR_USER_DISAGREES_LICENSE_AGREEMENT -> R.string.knox_standard_err_kpe_user_disagrees_license_agreement
            ERROR_ANOTHER_PROCESS_IN_PLACE -> R.string.knox_standard_err_kpe_another_process_in_place
            ERROR_LICENSE_DEACTIVATED -> R.string.knox_standard_err_kpe_license_deactivated
            ERROR_LICENSE_EXPIRED -> R.string.knox_standard_err_kpe_license_expired
            ERROR_LICENSE_QUANTITY_EXHAUSTED -> R.string.knox_standard_err_kpe_license_quantity_exhausted
            ERROR_LICENSE_ACTIVATION_NOT_FOUND -> R.string.knox_standard_err_kpe_license_activation_not_found
            ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE -> R.string.knox_standard_err_kpe_license_quantity_exhausted_on_auto_release
            else -> R.string.knox_standard_err_kpe_code_unknown
        }

        return try {
            when (resourceId) {
                R.string.knox_standard_err_kpe_code_unknown ->
                    resourceProvider.getString(resourceId, errorCode.toString())
                else -> resourceProvider.getString(resourceId)
            }
        } catch (e: MissingFormatArgumentException) {
            Log.e(tag, "MissingFormatArgumentException for resourceId: $resourceId", e)
            "Error retrieving message for error code: $errorCode"
        } catch (e: Exception) {
            Log.e(tag, "Exception retrieving error message for resourceId: $resourceId", e)
            "Unknown error occurred (code: $errorCode)"
        }
    }
}