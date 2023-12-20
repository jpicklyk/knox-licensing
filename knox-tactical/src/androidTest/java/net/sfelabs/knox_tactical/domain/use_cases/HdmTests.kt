package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.hdm.HdmManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 130)
class HdmTests {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkGetHdmPolicy_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "getHdmPolicy"))
    }

    /**
     * API stealthHwCpControl was deprecated in Android 13 MR1 (131) and replaced by a more
     * encompassing API to control all HDM policies.  See stealthHwControl()
     */
    @Test
    @TacticalSdkSuppress(maxReleaseVersion = 131)
    fun checkStealthHwCpControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthCpHwControl"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun checkStealthHwControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthHwControl"))
    }

    /*
   *   CP blocked: both deviceBlock  and compromiseBlock are 0x00000100
   *   CP unblocked: both deviceBlock  and compromiseBlock are 0x00000000

    @Test
    fun confirmStealthModeHdmEnabled() = runTest {
        val results = GetHdmPolicyUseCase(appContext).invoke()
        if(results is ApiCall.Success) {
            val policy = JSONObject(results.data)
            println("HDM Policy: $policy")
            TestCase.assertTrue(
                "HDM result is not SUCCESS",
                policy.getString("resultMessage") == "SUCCESS"
            )
            TestCase.assertTrue(
                "HDM device block is not 0x00000100",
                policy.getString("deviceBlock") == "0x00000100"
            )
            TestCase.assertTrue(
                "HDM compromise block is not 0x00000100",
                policy.getString("compromiseBlock") == "0x00000100"
            )
        } else {
            TestCase.assertTrue(results.toString(), false)
        }
    }
*/


}