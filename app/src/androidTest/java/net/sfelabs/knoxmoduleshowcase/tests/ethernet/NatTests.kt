package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.DefaultAsserter.assertTrue

@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141)
@RunWith(AndroidJUnit4::class)
class NatTests {

    @Test
    fun placeHolderTest() {
        assertTrue("Placeholder until tests are written", false)
    }
}