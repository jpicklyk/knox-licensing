package net.sfelabs.knoxmoduleshowcase.android10

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.common.core.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.NetworkInterface

@RunWith(AndroidJUnit4::class)
class MultiEthernetConfigurationTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()

    @Test
    fun setEthernetConfigurations_Exists() {
        assert(checkMethodExistence(systemManager::class, "setEthernetConfigurations"))
    }
    @Test
    fun setEthernetConfigurationsMultiDns_Exists() {
        assert(checkMethodExistence(systemManager::class, "setEthernetConfigurationsMultiDns"))
    }

    @Test
    fun setEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(settingsManager::class, "setEthernetAutoConnectionState"))
    }

    @Test
    fun getEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(settingsManager::class, "getEthernetAutoConnectionState"))
    }
}