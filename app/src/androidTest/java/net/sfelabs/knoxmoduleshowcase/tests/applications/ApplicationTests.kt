package net.sfelabs.knoxmoduleshowcase.tests.applications

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import net.sfelabs.knox.core.common.domain.isPackageInstalled
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ApplicationTests {
    private val pm = InstrumentationRegistry.getInstrumentation().targetContext.packageManager

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkMyFilesNetworkStorageIsIncluded() {
        assertTrue(
            "MyFiles Network Manager is not installed!",
            isPackageInstalled(
                packageName = "com.samsung.android.app.networkstoragemanager",
                packageManager = pm
                )
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatKidsModeHasBeenRemoved() {
        assertFalse(
            "com.sec.android.app.kidshome is still installed but should be removed",
            isPackageInstalled("com.sec.android.app.kidshome", pm),
        )
        assertFalse(
            "com.sec.android.app.kidsinstaller is still installed but should be removed",
            isPackageInstalled("com.sec.android.app.kidsinstaller", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatGameLauncherHasBeenRemoved() {
        assertFalse(
            "com.samsung.android.game.gamehome is still installed but should be removed",
            isPackageInstalled("com.samsung.android.game.gamehome", pm)
        )
        assertFalse(
            "com.samsung.android.game.gametools is still installed but should be removed",
            isPackageInstalled("com.samsung.android.game.gametools", pm)
        )
        assertFalse(
            "com.samsung.android.game.gos is still installed but should be removed",
            isPackageInstalled("com.samsung.android.game.gos", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatFacebookHasBeenRemoved() {
        assertFalse(
            "com.facebook.android is still installed but should be removed",
            isPackageInstalled("com.facebook.android", pm)
        )
        //preload
        assertFalse(
            "com.facebook.katana is still installed but should be removed",
            isPackageInstalled("com.facebook.katana", pm)

        )
        assertFalse(
            "com.facebook.appmanager is still installed but should be removed",
            isPackageInstalled("com.facebook.appmanager", pm)
        )
        assertFalse(
            "com.facebook.system is still installed but should be removed",
            isPackageInstalled("com.facebook.system", pm)
        )
        assertFalse(
            "com.facebook.services is still installed but should be removed",
            isPackageInstalled("com.facebook.services", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatNetflixHasBeenRemoved() {
        assertFalse(
            "com.netflix.mediaclient is still installed but should be removed",
            isPackageInstalled("com.netflix.mediaclient", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun checkThatSpotifyHasBeenRemoved() {
        assertFalse(
            "com.spotify.music is still installed but should be removed",
            isPackageInstalled("com.spotify.music", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatLinkedInHasBeenRemoved() {
        assertFalse(
            "com.linkedin.android is still installed but should be removed",
            isPackageInstalled("com.linkedin.android", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyDictationHasBeenRemoved() {
        assertFalse(
            "com.samsung.android.bixby.service is still installed but should be removed",
            isPackageInstalled("com.samsung.android.bixby.service", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVisionHasBeenRemoved() {
        assertFalse(
            "com.samsung.android.visionintelligence is still installed but should be removed",
            isPackageInstalled("com.samsung.android.visionintelligence", pm)
        )
        assertFalse(
            "com.samsung.android.bixbyvision.framework is still installed but should be removed",
            isPackageInstalled("com.samsung.android.bixbyvision.framework", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun checkThatBixbyVoiceHasBeenRemoved() {
        assertFalse(
            "com.samsung.android.bixby.agent is still installed but should be removed",
            isPackageInstalled("com.samsung.android.bixby.agent", pm)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 140)
    fun checkThatSamsungGlobalGoalsHasBeenRemoved() {
        assertFalse(
            "com.samsung.sree is still installed but should be removed",
            isPackageInstalled("com.samsung.sree", pm)
        )
    }

    // This cannot be removed as there are other components reliant on it that are required by Google
//    @Test
//    @TacticalSdkSuppress(minReleaseVersion = 132)
//    fun checkThatBixbyRoutinesHasBeenRemoved() {
//        assertFalse(isPackageInstalled("com.samsung.android.app.routines", pm))
//    }

}