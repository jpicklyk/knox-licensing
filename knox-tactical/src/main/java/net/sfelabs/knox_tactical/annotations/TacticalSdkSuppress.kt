package net.sfelabs.knox_tactical.annotations

import android.os.Build
import android.os.Debug
import android.provider.Settings
import android.provider.Settings.Global.ADB_ENABLED
import androidx.test.filters.AbstractFilter
import androidx.test.platform.app.InstrumentationRegistry
import net.sfelabs.knox_tactical.domain.model.VersionInfo
import org.junit.runner.Description

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TacticalSdkSuppress(
    val minReleaseVersion: Int = 1,
    val maxReleaseVersion: Int = Int.MAX_VALUE,
    val requiresUsbDebugging: Boolean = false,
    val requiresWifiDebugging: Boolean = false
) {

    @SuppressWarnings("unused")
    class Filter: AbstractFilter() {
        override fun describe(): String {
            return "Filter that determines if the running device supports the test case"
        }

        override fun evaluateTest(description: Description?): Boolean {

            val testAnnotation = description?.getAnnotation(TacticalSdkSuppress::class.java)
            val classAnnotation = description?.testClass?.getAnnotation(TacticalSdkSuppress::class.java)

            return deviceSupportsTest(testAnnotation) && deviceSupportsTest(classAnnotation)
        }

        private fun deviceSupportsTest(annotation: TacticalSdkSuppress?): Boolean {
            val versionInfo = VersionInfo(Build.DISPLAY.split(".").last())
            val versionCheck =  annotation == null || (
                    versionInfo.releaseVersion >= annotation.minReleaseVersion &&
                    versionInfo.releaseVersion <= annotation.maxReleaseVersion
                    )
            if(annotation != null && annotation.requiresUsbDebugging) {
                return versionCheck && isUsbDebuggingConnected()
            }
            if(annotation != null && annotation.requiresWifiDebugging) {
                return versionCheck && isWifiDebuggingConnected()
            }
            return versionCheck
        }

        /**
         * Unfortunately these only determine if the setting is on, it does not confirm that the
         * connection is made via USB or WiFi.  There does not appear to be a way to differentiate.
         */
        private fun isUsbDebuggingConnected(): Boolean {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val adbEnabled = Settings.Global.getInt(context.contentResolver, ADB_ENABLED)
            val connected = Debug.isDebuggerConnected()
            val result = connected && adbEnabled == 1
            return result
        }
        private fun isWifiDebuggingConnected(): Boolean {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val adbEnabled = Settings.Global.getInt(context.contentResolver, "adb_wifi_enabled", 0)
            val connected = Debug.isDebuggerConnected()
            val result = connected && adbEnabled == 0
            return result
        }

    }

}
