package net.sfelabs.knoxmoduleshowcase.manual_tests.experimental

import android.content.pm.PackageManager.FEATURE_TELEPHONY_DATA
import android.content.pm.PackageManager.FEATURE_TELEPHONY_SUBSCRIPTION
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelephonyTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val packageManager = context.packageManager

    @Test
    fun hasTelephonyData() {
        val hasTelephonyFeature = packageManager.hasSystemFeature(FEATURE_TELEPHONY_DATA)
        assert(hasTelephonyFeature)
    }

    @Test
    fun hasTelephonySubscription() {
        val hasTelephonyFeature = packageManager.hasSystemFeature(FEATURE_TELEPHONY_SUBSCRIPTION)
        assert(hasTelephonyFeature)
    }
}