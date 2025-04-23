package net.sfelabs.knoxmoduleshowcase.tests.applications

import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import net.sfelabs.knox.core.common.domain.isPackageInstalled
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@SmallTest
class ApplicationTests {
    private val pm = InstrumentationRegistry.getInstrumentation().targetContext.packageManager

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatKidsModeHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.sec.android.app.kidshome", pm),
            "com.sec.android.app.kidshome is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.sec.android.app.kidsinstaller", pm),
            "com.sec.android.app.kidsinstaller is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatGameLauncherHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.samsung.android.game.gamehome", pm),
            "com.samsung.android.game.gamehome is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.samsung.android.game.gametools", pm),
            "com.samsung.android.game.gametools is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.samsung.android.game.gos", pm),
            "com.samsung.android.game.gos is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatFacebookHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.facebook.android", pm),
            "com.facebook.android is still installed but should be removed"
        )
        //preload
        assertFalse(
            isPackageInstalled("com.facebook.katana", pm),
            "com.facebook.katana is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.facebook.appmanager", pm),
            "com.facebook.appmanager is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.facebook.system", pm),
            "com.facebook.system is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.facebook.services", pm),
            "com.facebook.services is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatNetflixHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.netflix.mediaclient", pm),
            "com.netflix.mediaclient is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatSpotifyHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.spotify.music", pm),
            "com.spotify.music is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatLinkedInHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.linkedin.android", pm),
            "com.linkedin.android is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyDictationHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.samsung.android.bixby.service", pm),
            "com.samsung.android.bixby.service is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVisionHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.samsung.android.visionintelligence", pm),
            "com.samsung.android.visionintelligence is still installed but should be removed"
        )
        assertFalse(
            isPackageInstalled("com.samsung.android.bixbyvision.framework", pm),
            "com.samsung.android.bixbyvision.framework is still installed but should be removed"
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVoiceHasBeenRemoved() {
        assertFalse(
            isPackageInstalled("com.samsung.android.bixby.agent", pm),
            "com.samsung.android.bixby.agent is still installed but should be removed"
        )
    }

    // This cannot be removed as there are other components reliant on it that are required by Google
//    @Test
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun checkThatBixbyRoutinesHasBeenRemoved() {
//        assertFalse(isPackageInstalled("com.samsung.android.app.routines", pm))
//    }

}