package net.sfelabs.knoxmoduleshowcase.di

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class KnoxInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("net.sfelabs.knoxmoduleshowcase", appContext.packageName)
    }

    @Test
    fun getCustomDeviceManager() {
        val cdm = CustomDeviceManager.getInstance()
        assert(cdm != null)
    }

    @Test
    fun getSystemManager() {
        val cdm = CustomDeviceManager.getInstance()
        val sm = cdm.systemManager
        assert(sm != null)
    }
}