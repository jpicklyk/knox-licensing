package net.sfelabs.knox_tactical.annotations

import android.os.Build
import androidx.test.filters.AbstractFilter
import net.sfelabs.knox_tactical.domain.model.VersionInfo
import org.junit.runner.Description

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TacticalSdkSuppress(
    val minReleaseVersion: Int = 1,
    val maxReleaseVersion: Int = Int.MAX_VALUE
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
            return annotation == null || (
                    versionInfo.releaseVersion >= annotation.minReleaseVersion &&
                    versionInfo.releaseVersion <= annotation.maxReleaseVersion
                    )
        }
    }

}
