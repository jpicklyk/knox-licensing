package net.sfelabs.knoxmoduleshowcase.receivers

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import net.sfelabs.knoxmoduleshowcase.R


class DeviceOwnerReceiver: DeviceAdminReceiver() {
    /**
     * Called on a new profile when device owner provisioning has completed.  Device Owner
     * provisioning is the process of setting up the device so that its main profile is managed by
     * this application configured as the device owner.
     */
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        //Enable the profile
        val manager: DevicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = getComponentName(context)
        manager.setProfileName(componentName, context.getString(R.string.profile_name))
    }

    /**
     * @return A newly instantiated [android.content.ComponentName] for this
     * DeviceAdminReceiver.
     */
    private fun getComponentName(context: Context): ComponentName {
        return ComponentName(context.applicationContext, DeviceOwnerReceiver::class.java)
    }
}