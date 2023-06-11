package net.sfelabs.knoxmoduleshowcase.di

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import net.sfelabs.common.core.di.AndroidServiceModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AndroidServicesTests {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }
    @Test
    fun getWifiManager() {
        val service = AndroidServiceModule.provideWifiManager(context)
        assert(service != null)
    }

    @Test
    fun getConnectivityManager() {
        val service = AndroidServiceModule.provideConnectivityManager(context)
        assert(service != null)
    }
}