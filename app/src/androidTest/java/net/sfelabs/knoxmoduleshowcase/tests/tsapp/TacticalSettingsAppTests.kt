package net.sfelabs.knoxmoduleshowcase.tests.tsapp

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class TacticalSettingsAppTests {
    private val te3Xc6pRegex = """^TP1A\.220624\.014\.G736U1UE.+_B2BF*""".toRegex()
    private val te3S23Regex = """^TP1A\.220624\.014\.S911U1UE.+_B2BF*""".toRegex()

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun testBuildNumberMatchesTacticalEdition3RegexFormat() {
        val buildNumber = Build.DISPLAY

        // Log the build number for easy viewing in test results
        println("Device Build Number: $buildNumber")

        // Assert that the build number is not null or empty
        assertNotNull("Build number should not be null", buildNumber)
        assertTrue("Build number should not be empty", buildNumber.isNotEmpty())

        assertTrue(
            "Build number should match expected format",
            buildNumber.matches(te3S23Regex) or buildNumber.matches(te3Xc6pRegex)
        )
    }
}