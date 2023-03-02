package net.sfelabs.knoxmoduleshowcase.features.permissions

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import net.sfelabs.knoxmoduleshowcase.app.receivers.AdminReceiver

fun isDeviceAdminGranted(context: Context): Boolean {
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val deviceAdminComponentName = ComponentName(context, AdminReceiver::class.java)
    return devicePolicyManager.isAdminActive(deviceAdminComponentName)
}

fun requestDeviceAdmin(context: Context) {
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
    val deviceAdminComponentName = ComponentName(context, AdminReceiver::class.java)
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName)
    intent.putExtra(
        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
        "This Knox enabled application requires the device admin permissions to operate correctly."
    )
    val activity = context as Activity
    startActivityForResult(activity, intent, 39214, null)

}