package net.sfelabs.knox_common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager

class LicenseReceiver: BroadcastReceiver() {
    private val DEFAULT_ERROR_CODE = -1
    private val DEFAULT_RESULT_TYPE = -1

    private fun showToast(ctx: Context?, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == null) {
            // No intent action is available
            showToast(context, context.resources.getString(R.string.no_intent_action))
        } else if (action == KnoxEnterpriseLicenseManager.ACTION_LICENSE_STATUS) {

            val errorCode = intent.getIntExtra(
                KnoxEnterpriseLicenseManager.EXTRA_LICENSE_ERROR_CODE,
                DEFAULT_ERROR_CODE
            )
            val resultType = intent.getIntExtra(
                KnoxEnterpriseLicenseManager.EXTRA_LICENSE_RESULT_TYPE,
                DEFAULT_RESULT_TYPE
                )
            when (resultType) {
                KnoxEnterpriseLicenseManager.LICENSE_RESULT_TYPE_ACTIVATION -> {
                    handleActivationResult(context, errorCode)
                }
                KnoxEnterpriseLicenseManager.LICENSE_RESULT_TYPE_VALIDATION -> {
                    handleValidationResult(context, errorCode)
                }
                KnoxEnterpriseLicenseManager.LICENSE_RESULT_TYPE_DEACTIVATION -> {
                    handleDeactivationResult(context, errorCode)
                }
                else -> {
                    showToast(context, "License result type is not available")
                }
            }

        }

    }


    private fun handleActivationResult(context: Context, errorCode: Int) {
        if (errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
            // ELM activated successfully
            showToast(
                context,
                context.resources.getString(R.string.kpe_activated_succesfully)
            )
            Log.d(
                "KnoxLicenseReceiver",
                context.getString(R.string.kpe_activated_succesfully)
            )
        } else {
            // KPE activation failed
            // Display KPE error message
            val errorMessage: String = getKPEErrorMessage(context, errorCode)
            showToast(context, errorMessage)
            Log.d("KnoxLicenseReceiver", "Activation of Knox license error: $errorMessage")
        }
    }
    private fun handleValidationResult(context: Context, errorCode: Int) {
        val errorMessage: String = getKPEErrorMessage(context, errorCode)
        Log.d("KnoxLicenseReceiver", "Validation of Knox license result: $errorMessage")
    }

    private fun handleDeactivationResult(context: Context, errorCode: Int) {
        if(errorCode == KnoxEnterpriseLicenseManager.ERROR_NONE) {
            showToast(
                context,
                context.resources.getString(R.string.kpe_deactivated_successfully)
            )
            Log.d(
                "KnoxLicenseReceiver",
                context.getString(R.string.kpe_deactivated_successfully)
            )
        } else {
            val errorMessage: String = getKPEErrorMessage(context, errorCode)
            Log.d("KnoxLicenseReceiver", "Deactivation of Knox license error: $errorMessage")
        }
    }

    private fun getKPEErrorMessage(context: Context, errorCode: Int): String {
        val message: String = when (errorCode) {
            KnoxEnterpriseLicenseManager.ERROR_INTERNAL -> context.resources.getString(R.string.err_kpe_internal)
            KnoxEnterpriseLicenseManager.ERROR_INTERNAL_SERVER -> context.resources.getString(R.string.err_kpe_internal_server)
            KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE -> context.resources.getString(R.string.err_kpe_licence_invalid_license)
            KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME -> context.resources.getString(R.string.err_kpe_invalid_package_name)
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_TERMINATED -> context.resources.getString(R.string.err_kpe_licence_terminated)
            KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED -> context.resources.getString(R.string.err_kpe_network_disconnected)
            KnoxEnterpriseLicenseManager.ERROR_NETWORK_GENERAL -> context.resources.getString(R.string.err_kpe_network_general)
            KnoxEnterpriseLicenseManager.ERROR_NOT_CURRENT_DATE -> context.resources.getString(R.string.err_kpe_not_current_date)
            KnoxEnterpriseLicenseManager.ERROR_NULL_PARAMS -> context.resources.getString(R.string.err_kpe_null_params)
            KnoxEnterpriseLicenseManager.ERROR_UNKNOWN -> context.resources.getString(R.string.err_kpe_unknown)
            KnoxEnterpriseLicenseManager.ERROR_USER_DISAGREES_LICENSE_AGREEMENT -> context.resources.getString(
                R.string.err_kpe_user_disagrees_license_agreement
            )
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_DEACTIVATED -> context.resources.getString(R.string.err_kpe_license_deactivated)
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_EXPIRED -> context.resources.getString(R.string.err_kpe_license_expired)
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED -> context.resources.getString(
                R.string.err_kpe_license_quantity_exhausted
            )
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_ACTIVATION_NOT_FOUND -> context.resources.getString(
                R.string.err_kpe_license_activation_not_found
            )
            KnoxEnterpriseLicenseManager.ERROR_LICENSE_QUANTITY_EXHAUSTED_ON_AUTO_RELEASE -> context.resources.getString(
                R.string.err_kpe_license_quantity_exhausted_on_auto_release
            )
            else -> {
                // Unknown error code
                context.resources
                    .getString(
                        R.string.err_kpe_code_unknown,
                        errorCode.toString(),
                        null
                    )
            }
        }
        return message
    }
}