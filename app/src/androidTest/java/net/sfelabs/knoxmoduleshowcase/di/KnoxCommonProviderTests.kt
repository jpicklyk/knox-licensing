package net.sfelabs.knoxmoduleshowcase.di

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import net.sfelabs.knox_common.di.KnoxModule as CommonModule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class KnoxCommonProviderTests {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }
    @Test
    fun useAppContext() {
        assertEquals("net.sfelabs.knoxmoduleshowcase", context.packageName)
    }

    @Test
    fun getKnoxLicenseManager() {
        val manager = CommonModule.provideKnoxEnterpriseLicenseManager(context)
        assert(manager != null)
    }

    @Test
    fun getSystemManager() {
        val cdm = CustomDeviceManager.getInstance()
        val sm = cdm.systemManager
        assert(sm != null)
    }
}