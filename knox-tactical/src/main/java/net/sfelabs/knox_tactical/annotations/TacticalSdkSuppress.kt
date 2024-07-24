package net.sfelabs.knox_tactical.annotations

import android.os.Build
import androidx.test.filters.AbstractFilter
import net.sfelabs.knox_tactical.domain.model.TacticalEditionReleases
import org.junit.runner.Description

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TacticalSdkSuppress(
    val minReleaseVersion: Int = 1,
    val maxReleaseVersion: Int = Int.MAX_VALUE,
    val excludeModels: Array<String> = [],
    val includeModels: Array<String> = []
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
            if (annotation == null) return true

            val versionInfo = TacticalEditionReleases.getVersionInfo(Build.DISPLAY.substringAfterLast("."))
            val versionCheck = versionInfo.releaseVersion in annotation.minReleaseVersion..annotation.maxReleaseVersion

            return versionCheck &&
                    (versionInfo.modelName !in annotation.excludeModels ||
                        versionInfo.modelName in annotation.includeModels)
        }
    }
}
