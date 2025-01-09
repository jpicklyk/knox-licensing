package net.sfelabs.knoxmoduleshowcase.tests.applications

import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import net.sfelabs.core.domain.isPackageInstalled
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@SmallTest
class ApplicationTests {
    private val pm = InstrumentationRegistry.getInstrumentation().targetContext.packageManager

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatKidsModeHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.sec.android.app.kidshome", pm))
        assertFalse(isPackageInstalled("com.sec.android.app.kidsinstaller", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatGameLauncherHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.game.gamehome", pm))
        assertFalse(isPackageInstalled("com.samsung.android.game.gametools", pm))
        assertFalse(isPackageInstalled("com.samsung.android.game.gos", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatFacebookHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.facebook.android", pm))
        //preload
        assertFalse(isPackageInstalled("com.facebook.katana", pm))
        assertFalse(isPackageInstalled("com.facebook.appmanager", pm))
        assertFalse(isPackageInstalled("com.facebook.system", pm))
        assertFalse(isPackageInstalled("com.facebook.services", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatNetflixHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.netflix.mediaclient", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatSpotifyHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.spotify.music", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatLinkedInHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.linkedin.android", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyDictationHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.bixby.service", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVisionHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.visionintelligence", pm))
        assertFalse(isPackageInstalled("com.samsung.android.bixbyvision.framework", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVoiceHasBeenRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.bixby.agent", pm))
    }

    // This cannot be removed as there are other components reliant on it that are required by Google
//    @Test
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun checkThatBixbyRoutinesHasBeenRemoved() {
//        assertFalse(isPackageInstalled("com.samsung.android.app.routines", pm))
//    }

}