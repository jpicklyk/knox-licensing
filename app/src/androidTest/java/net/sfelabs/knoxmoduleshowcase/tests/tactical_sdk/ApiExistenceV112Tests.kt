package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 112.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 112)
class ApiExistenceV112Tests {

    // region SystemManager

    @Test
    fun stopPPPD_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "stopPPPD")) { "Expected method 'stopPPPD' to exist on SystemManager" }
    }

    // endregion
}
