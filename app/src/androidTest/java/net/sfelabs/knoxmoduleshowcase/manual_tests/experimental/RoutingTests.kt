package net.sfelabs.knoxmoduleshowcase.manual_tests.experimental

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ListIpAddressesUseCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Customized routing using the Linux IP Rules.  See Linux manual page for details on commands:
 * https://man7.org/linux/man-pages/man8/ip-rule.8.html
 *
 * The raw commands can be passed through the TE API executeAdbCommand using the IP header.
 *
 * Some points to consider for a full implementation:
 * 1. Rules can only be configured when the interface is up
 * 2. If the interface goes down, the rules need to be reconfigured when it comes back up.
 */
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class RoutingTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val packageManager = AndroidServiceModule.providePackageManager(context)
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    /**
     * Test requires eth0 connection to function
     * Rules need to be tracked manually and deleted manually
     *
     * Scenario 1:
     * 	As an IT Admin, I want to configure the following routes 192.168.0.3 and 192.168.0.4 for
     * 	the application "com.android.chrome" to go through eth0 and block all other routes;
     * 	Meaning that for connections directed to 192.168.0.3 or 192.168.0.4 to be routed over eth0
     * 	with src IP or whatever is assigned.
     *
     * Solution:
     * 		Assume the uid of the chrome application is "10256";
     * 		Assume the IP address of network interface eth0 is 192.168.1.2;
     * 	    Blackhole all packets that don't match the above rule
     *
     * 		ip route add 192.168.0.3 dev eth0 src 192.168.1.2 table 70;
     * 		ip route add 192.168.0.4 dev eth0 src 192.168.1.2 table 70;
     * 		ip rule add from all uidrange 10256-10256 lookup 70 prio 10;
     * 	    ip rule add from all uidrange {uid}-{uid} blackhole prio 11;
     */
    @Test
    fun routeChromeOnlyThroughEth0() = runTest {
        val packageName = "com.android.chrome"
        val chromeUid = packageManager.getPackageUid(packageName, 0)
        val ipSrc = "192.168.2.244"
        val result = ListIpAddressesUseCase(settingsManager).invoke("eth0")
        assert(result is ApiResult.Success)
        if(result is ApiResult.Success) {
            //val ipList = result.data
            val route1 = "route add 192.168.0.3 dev eth0 src $ipSrc table 70"
            val route2 = "route add 192.168.0.4 dev eth0 src $ipSrc table 70"
            val rule = "rule add from all uidrange ${chromeUid}-${chromeUid} lookup 70 prio 10"
            val block = "rule add from all uidrange ${chromeUid}-${chromeUid} blackhole prio 11"

            val route1Result = ExecuteAdbCommandUseCase(systemManager).invoke(
                AdbHeader.IP,
                route1
            )
            assert(route1Result is ApiResult.Success)

            val route2Result = ExecuteAdbCommandUseCase(systemManager).invoke(
                AdbHeader.IP,
                route2
            )
            assert(route2Result is ApiResult.Success)

            val ruleResult = ExecuteAdbCommandUseCase(systemManager).invoke(
                AdbHeader.IP,
                rule
            )
            assert(ruleResult is ApiResult.Success)

            val blockResult = ExecuteAdbCommandUseCase(systemManager).invoke(
                AdbHeader.IP,
                block
            )
            assert(blockResult is ApiResult.Success)
        }

    }

    /**
     * Scenario 1:
     * 	As an IT Admin, I want to configure the following routes 192.168.0.3 and 192.168.0.4 for the application "com.android.chrome" to go through eth0;
     * Solution:
     * 		Assume the uid of the chrome application is "10256";
     * 		Assume the IP address of network interface eth0 is 192.168.1.2;
     *
     * 		ip route add 192.168.0.3 dev eth0 src 192.168.1.2 table 70;
     * 		ip route add 192.168.0.4 dev eth0 src 192.168.1.2 table 70;
     * 		ip rule add from uidrange 10256-10256 lookup 70 prio 10;
     */
}