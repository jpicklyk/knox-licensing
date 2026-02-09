package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.rules.AdbUsbRequired
import net.sfelabs.knox.core.testing.rules.AdbUsbRequiredRule
import net.sfelabs.knox.core.testing.rules.EthernetRequired
import net.sfelabs.knox.core.testing.rules.EthernetRequiredRule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.nat.EnableIpForwardingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.nat.ExecuteIptablesCommandUseCase
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for NAT configuration APIs: [ExecuteIptablesCommandUseCase] and
 * [EnableIpForwardingUseCase].
 *
 * ## Prerequisites
 * - Device Administrator privileges (Knox Tactical SDK)
 * - Tactical SDK release version 141+
 * - Ethernet adapter connected for tests annotated with [EthernetRequired]
 *
 * ## ADB Connection Warning
 * Several tests modify iptables rules or IP forwarding on wlan0, which **will disrupt
 * a WiFi ADB connection**. These tests are annotated with [AdbUsbRequired] and will be
 * automatically skipped when USB debugging is not detected.
 *
 * To run the full test suite, connect ADB via USB. Over WiFi ADB, only the eth0 and
 * input validation tests will execute.
 *
 * ## Cleanup
 * An [AfterClass] method runs after all tests to restore the device state:
 * - Flushes NAT and FORWARD iptables rules
 * - Resets FORWARD policy to DROP
 * - Disables IP forwarding on both eth0 and wlan0
 */
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141)
@RunWith(AndroidJUnit4::class)
class NatTests {

    @get:Rule
    val adbUsbRule = AdbUsbRequiredRule()

    @get:Rule
    val ethernetRule = EthernetRequiredRule()

    companion object {
        private const val TAG = "NatTests"
        private const val IFACE_WLAN = "wlan0"
        private const val IFACE_ETH = "eth0"

        @JvmStatic
        @AfterClass
        fun cleanup(): Unit = runBlocking {
            val iptables = ExecuteIptablesCommandUseCase()
            val ipForwarding = EnableIpForwardingUseCase()

            Log.i(TAG, "Cleaning up: flushing iptables rules and disabling IP forwarding")

            iptables("iptables -t nat -F")
            iptables("iptables -F FORWARD")
            iptables("iptables -P FORWARD DROP")

            ipForwarding(IFACE_ETH, enable = false)
            ipForwarding(IFACE_WLAN, enable = false)

            Log.i(TAG, "Cleanup complete")
        }
    }

    private val executeIptablesCommand = ExecuteIptablesCommandUseCase()
    private val enableIpForwarding = EnableIpForwardingUseCase()

    // region ExecuteIptablesCommandUseCase - valid commands

    @Test
    @AdbUsbRequired
    @EthernetRequired
    fun executePostRoutingMasquerade() = runTest {
        val cmd = "iptables -t nat -A POSTROUTING -o $IFACE_WLAN -j MASQUERADE"
        val result = executeIptablesCommand(cmd)
        logResult("POSTROUTING masquerade", cmd, result)
    }

    @Test
    @AdbUsbRequired
    @EthernetRequired
    fun executeForwardAcceptEstablished() = runTest {
        val cmd = "iptables -A FORWARD -i $IFACE_WLAN -o $IFACE_ETH -m state --state RELATED,ESTABLISHED -j ACCEPT"
        val result = executeIptablesCommand(cmd)
        logResult("FORWARD ESTABLISHED", cmd, result)
    }

    @Test
    @AdbUsbRequired
    @EthernetRequired
    fun executeForwardAccept() = runTest {
        val cmd = "iptables -A FORWARD -i $IFACE_ETH -o $IFACE_WLAN -j ACCEPT"
        val result = executeIptablesCommand(cmd)
        logResult("FORWARD ACCEPT", cmd, result)
    }

    @Test
    fun executeSetForwardPolicyAccept() = runTest {
        val cmd = "iptables -P FORWARD ACCEPT"
        val result = executeIptablesCommand(cmd)
        logResult("FORWARD policy ACCEPT", cmd, result)
    }

    @Test
    fun executeSetForwardPolicyDrop() = runTest {
        val cmd = "iptables -P FORWARD DROP"
        val result = executeIptablesCommand(cmd)
        logResult("FORWARD policy DROP", cmd, result)
    }

    // endregion

    // region ExecuteIptablesCommandUseCase - input validation

    @Test
    fun rejectEmptyCommand() = runTest {
        val result = executeIptablesCommand("")
        logResult("reject empty", "", result)
        assertTrue("Empty command should be rejected", result is ApiResult.Error)
    }

    @Test
    fun rejectUnsupportedCommand() = runTest {
        val cmd = "iptables -L"
        val result = executeIptablesCommand(cmd)
        logResult("reject unsupported", cmd, result)
        assertTrue("Unsupported command should be rejected", result is ApiResult.Error)
    }

    @Test
    fun rejectArbitraryCommand() = runTest {
        val cmd = "rm -rf /"
        val result = executeIptablesCommand(cmd)
        logResult("reject arbitrary", cmd, result)
        assertTrue("Arbitrary command should be rejected", result is ApiResult.Error)
    }

    @Test
    fun rejectUppercaseCommand() = runTest {
        val cmd = "IPTABLES -P FORWARD ACCEPT"
        val result = executeIptablesCommand(cmd)
        logResult("reject uppercase", cmd, result)
        assertTrue("Uppercase command should be rejected", result is ApiResult.Error)
    }

    // endregion

    // region EnableIpForwardingUseCase

    @Test
    @EthernetRequired
    fun enableIpForwardingOnEth0() = runTest {
        val result = enableIpForwarding(IFACE_ETH, enable = true)
        logResult("enableIpForwarding", "$IFACE_ETH, enable=true", result)
    }

    @Test
    @EthernetRequired
    fun disableIpForwardingOnEth0() = runTest {
        val result = enableIpForwarding(IFACE_ETH, enable = false)
        logResult("enableIpForwarding", "$IFACE_ETH, enable=false", result)
    }

    @Test
    @AdbUsbRequired
    @EthernetRequired
    fun enableIpForwardingOnWlan0() = runTest {
        val result = enableIpForwarding(IFACE_WLAN, enable = true)
        logResult("enableIpForwarding", "$IFACE_WLAN, enable=true", result)
    }

    @Test
    @AdbUsbRequired
    @EthernetRequired
    fun disableIpForwardingOnWlan0() = runTest {
        val result = enableIpForwarding(IFACE_WLAN, enable = false)
        logResult("enableIpForwarding", "$IFACE_WLAN, enable=false", result)
    }

    // endregion

    private fun logResult(label: String, input: String, result: ApiResult<*>) {
        Log.i(TAG, "--- $label ---")
        Log.i(TAG, "  Input:  $input")
        when (result) {
            is ApiResult.Success -> {
                Log.i(TAG, "  Status: SUCCESS")
                Log.i(TAG, "  Data:   '${result.data}'")
                Log.i(TAG, "  Type:   ${result.data?.let { it::class.java.name } ?: "null"}")
            }
            is ApiResult.Error -> {
                Log.i(TAG, "  Status: ERROR")
                Log.i(TAG, "  Error:  ${result.apiError}")
                Log.i(TAG, "  Except: ${result.exception}")
            }
            is ApiResult.NotSupported -> {
                Log.i(TAG, "  Status: NOT_SUPPORTED")
            }
        }
    }
}
