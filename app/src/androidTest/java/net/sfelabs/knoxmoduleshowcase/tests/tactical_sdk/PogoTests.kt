package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 110, includeModels = ["SM-X308U"])
class PogoTests {
    @Test
    fun disablePOGOKeyboardConnection_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disablePOGOKeyboardConnection")) { "Expected method 'disablePOGOKeyboardConnection' to exist on SystemManager" }
    }

    @Test
    fun isPOGOKeyboardConnectionDisabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isPOGOKeyboardConnectionDisabled")) { "Expected method 'isPOGOKeyboardConnectionDisabled' to exist on SystemManager" }
    }
}
