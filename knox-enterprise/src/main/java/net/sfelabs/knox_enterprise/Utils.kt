package net.sfelabs.knox_enterprise

import android.content.Context
import android.os.Build
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager


fun activateLicense(context: Context) {
    // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
    val licenseManager = KnoxEnterpriseLicenseManager.getInstance(context.applicationContext)
    // License Activation TODO Add license key to Constants.java
    if(Build.MODEL == "SM-G981U1") {
        licenseManager.activateLicense(Constants.TE2_LICENSE_KEY)
    } else {
        licenseManager.activateLicense(Constants.TE3_LICENSE_KEY)
    }
}

fun deactivateLicense(context: Context) {
    val licenseManager = KnoxEnterpriseLicenseManager.getInstance(context.applicationContext)
    if(Build.MODEL == "SM-G981U1") {
        licenseManager.deActivateLicense(Constants.TE2_LICENSE_KEY)
    } else {
        licenseManager.deActivateLicense(Constants.TE3_LICENSE_KEY)
    }
}
