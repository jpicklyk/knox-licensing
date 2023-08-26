package net.sfelabs.knoxmoduleshowcase.android13

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.EnterpriseDeviceManager
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HdmTests {
    private lateinit var context: Context
    private lateinit var enterpriseDeviceManager: EnterpriseDeviceManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        enterpriseDeviceManager = KnoxModule.provideKnoxEnterpriseDeviceManager(context)
    }

    /*
    *   CP blocked: both deviceBlock  and compromiseBlock are 0x00000100
    *   CP unblocked: both deviceBlock  and compromiseBlock are 0x00000000
     */
    @Test
    fun confirmStealthModeHdmEnabled() = runTest {
        val results = GetHdmPolicyUseCase(enterpriseDeviceManager).invoke()
        if(results is ApiCall.Success) {
            val policy = JSONObject(results.data)
            println("HDM Policy: $policy")
            assertTrue("HDM result is not SUCCESS", policy.getString("resultMessage") == "SUCCESS")
            assertTrue("HDM device block is not 0x00000100", policy.getString("deviceBlock") == "0x00000100")
            assertTrue("HDM compromise block is not 0x00000100", policy.getString("compromiseBlock") == "0x00000100")
        } else {
            assertTrue(results.toString(), false)
        }
    }
}