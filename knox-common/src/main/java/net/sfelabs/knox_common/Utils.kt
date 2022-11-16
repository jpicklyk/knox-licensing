package net.sfelabs.knox_common

import android.content.Context
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager


fun activateLicense(context: Context) {
    // Instantiate the KnoxEnterpriseLicenseManager class to use the activateLicense method
    val licenseManager = KnoxEnterpriseLicenseManager.getInstance(context.applicationContext)
    // License Activation TODO Add license key to Constants.java
    licenseManager.activateLicense(Constants.KPE_LICENSE_KEY)
}

fun deactivateLicense(context: Context) {
    val licenseManager = KnoxEnterpriseLicenseManager.getInstance(context.applicationContext)
    licenseManager.deActivateLicense(Constants.KPE_LICENSE_KEY)
}
