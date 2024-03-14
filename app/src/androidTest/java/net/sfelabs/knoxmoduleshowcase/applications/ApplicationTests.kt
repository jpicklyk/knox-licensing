package net.sfelabs.knoxmoduleshowcase.applications

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertFalse
import net.sfelabs.core.domain.isPackageInstalled
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class ApplicationTests {
    private val pm = InstrumentationRegistry.getInstrumentation().targetContext.packageManager

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun kidsModeRemoved() {
        assertFalse(isPackageInstalled("com.sec.android.app.kidshome", pm))
        assertFalse(isPackageInstalled("com.sec.android.app.kidsinstaller", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun gameLauncherRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.game.gamehome", pm))
        assertFalse(isPackageInstalled("com.samsung.android.game.gametools", pm))
        assertFalse(isPackageInstalled("com.samsung.android.game.gos", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun facebookRemoved() {
        assertFalse(isPackageInstalled("com.facebook.android", pm))
        //preload
        assertFalse(isPackageInstalled("com.facebook.katana", pm))
        assertFalse(isPackageInstalled("com.facebook.appmanager", pm))
        assertFalse(isPackageInstalled("com.facebook.system", pm))
        assertFalse(isPackageInstalled("com.facebook.services", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun netflixRemoved() {
        assertFalse(isPackageInstalled("com.netflix.mediaclient", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun spotifyRemoved() {
        assertFalse(isPackageInstalled("com.spotify.music", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun linkedInRemoved() {
        assertFalse(isPackageInstalled("com.linkedin.android", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun bixbyDictationRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.bixby.service", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun bixbyVisionRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.visionintelligence", pm))
        assertFalse(isPackageInstalled("com.samsung.android.bixbyvision.framework", pm))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun bixbyVoiceRemoved() {
        assertFalse(isPackageInstalled("com.samsung.android.bixby.agent", pm))
    }

}