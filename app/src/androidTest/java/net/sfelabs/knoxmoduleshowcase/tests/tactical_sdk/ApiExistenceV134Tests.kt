package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 134.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 134)
class ApiExistenceV134Tests {

    // region SettingsManager

    @Test
    fun isAlwaysRadioOn_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "isAlwaysRadioOn")) { "Expected method 'isAlwaysRadioOn' to exist on SettingsManager" }
    }

    @Test
    fun setAlwaysRadioOn_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setAlwaysRadioOn")) { "Expected method 'setAlwaysRadioOn' to exist on SettingsManager" }
    }

    // endregion
}
