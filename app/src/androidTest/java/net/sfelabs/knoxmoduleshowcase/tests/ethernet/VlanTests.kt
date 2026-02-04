package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.rules.EthernetRequired
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@EthernetRequired
@TacticalSdkSuppress(minReleaseVersion = 141)
class VlanTests {

    companion object {
        private const val TAG = "VlanTests"
        private const val PARENT_INTERFACE = "eth0"
        private const val VLAN_INTERFACE = "eth0.2"
        private const val VLAN_ID = 2
        private const val IP_ADDRESS = "10.0.2.3/24"
    }

    private val executeAdbCommand = ExecuteAdbCommandUseCase()

    @Test
    fun configureEthernetVlan() = runTest {
        val commands = listOf(
            "link add link $PARENT_INTERFACE name $VLAN_INTERFACE type vlan id $VLAN_ID",
            "link set dev $VLAN_INTERFACE up",
            "addr add $IP_ADDRESS dev $VLAN_INTERFACE"
        )

        for (command in commands) {
            val result = executeAdbCommand(AdbHeader.IP, command)
            assert(result is ApiResult.Success) { "Command failed: $command" }
            Thread.sleep(500)
        }

        // Knox API doesn't return output - verify via adb shell
        Log.i(TAG, "VERIFY: adb shell ip link show $VLAN_INTERFACE")
        Log.i(TAG, "VERIFY: adb shell ip addr show $VLAN_INTERFACE")
    }

    //@After
    fun cleanup() = runTest {
        executeAdbCommand(AdbHeader.IP, "link delete $VLAN_INTERFACE")
    }
}