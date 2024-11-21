package net.sfelabs.knoxmoduleshowcase.tests.tunneling

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 132)
class LinuxTunnelingTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectivityManager = AndroidServiceModule.provideConnectivityManager(context)
    }

    /**
     * Assume you have two servers: Android device (node1) 192.168.2.200 and node2 192.168.2.201.
     * This will set up an ipip tunnel between these two hosts.
     */
    @Test
    fun configureIpipTunnel() = runTest {
        val tunnelName = "ipip0"
        val localAddr = "10.10.10.11"
        val remotePubAddr = "192.168.4.94"
        val internalAddr = "10.1.1.0/24"
        val remoteInternalSubnet = "/24"
        val commands = listOf(
            "link name $tunnelName type ipip local $localAddr remote $remotePubAddr",
            "link set $tunnelName up",
        )

        //"addr add $internalAddr dev $tunnelName",
        //"route add $remoteInternalSubnet dev $tunnelName"
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assert(result)
    }
}