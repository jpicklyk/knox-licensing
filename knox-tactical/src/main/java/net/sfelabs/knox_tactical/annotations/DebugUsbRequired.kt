package net.sfelabs.knox_tactical.annotations

import android.os.Debug
import androidx.test.filters.AbstractFilter
import androidx.test.filters.CustomFilter
import org.junit.runner.Description

/**
 * Unfortunately AndroidJUnit isn't supporting this customfilter fully yet as it is only experimental.
 * This seems very promising and a better way to approach filtering.
 */
@CustomFilter(filterClass = DebugUsbRequired.UsbDebuggingRequiredFilter::class)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DebugUsbRequired() {

    class UsbDebuggingRequiredFilter: AbstractFilter() {
        override fun describe(): String {
            return "Filter to ensure that the device is connected via USB for debugging"
        }

        override fun evaluateTest(description: Description?): Boolean {
            val testAnnotation = description?.getAnnotation(DebugUsbRequired::class.java)
            val classAnnotation = description?.testClass?.getAnnotation(DebugUsbRequired::class.java)

            return deviceSupportsTest(testAnnotation) && deviceSupportsTest(classAnnotation)
        }

        private fun deviceSupportsTest(annotation: DebugUsbRequired?): Boolean {
            val connected = Debug.isDebuggerConnected()
            return annotation == null || !connected
        }

    }
}
