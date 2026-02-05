package net.sfelabs.knoxmoduleshowcase.tests.adb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.adb.GuardAdbOverWlanUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [GuardAdbOverWlanUseCase] which adds IP routing rules to ensure
 * ADB traffic routes over wlan0 even when other interfaces (like ethernet) become default.
 *
 * These tests require:
 * - WiFi to be connected (wlan0 must be active)
 * - Device Administrator privileges (Knox executeAdbCommand API)
 *
 * The guard works by adding an IP rule that forces UID 2000 (shell/ADB daemon)
 * to use the wlan0 routing table with high priority.
 */
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class GuardAdbOverWlanTests {

    private lateinit var uiDevice: UiDevice
    private var discoveredTableId: Int? = null

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @After
    fun cleanup() = runTest {
        // Remove the guard rule if it was added
        discoveredTableId?.let { tableId ->
            // Use Knox API to remove the rule (best effort cleanup)
            val removeCommand = "rule del from all uidrange ${GuardAdbOverWlanUseCase.ADB_UID}-${GuardAdbOverWlanUseCase.ADB_UID} lookup $tableId prio ${GuardAdbOverWlanUseCase.RULE_PRIORITY}"
            net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase()(
                net.sfelabs.knox_tactical.domain.model.AdbHeader.IP,
                removeCommand
            )
        }
    }

    @Test
    fun parseWlanTableId_findsTableFromRuleOutput() {
        // Sample output from `ip rule show` containing wlan0 entry
        val sampleOutput = """
            0:	from all lookup local
            10000:	from all fwmark 0xc0000/0xd0000 lookup 99
            11000:	from all iif lo oif dummy0 uidrange 0-0 lookup 1002
            11000:	from all iif lo oif wlan0 uidrange 0-0 lookup 1037
            17000:	from all iif lo oif dummy0 lookup 1002
            17000:	from all iif lo oif wlan0 lookup 1037
            31000:	from all fwmark 0x0/0xffff iif lo lookup 1037
            32000:	from all unreachable
        """.trimIndent()

        val tableId = GuardAdbOverWlanUseCase.parseWlanTableId(sampleOutput)

        assertNotNull("Should find wlan0 table ID", tableId)
        assertEquals("Should extract correct table ID", 1037, tableId)
    }

    @Test
    fun parseWlanTableId_returnsNullWhenNoWlan0() {
        val sampleOutput = """
            0:	from all lookup local
            17000:	from all iif lo oif eth0 lookup 1043
            32000:	from all unreachable
        """.trimIndent()

        val tableId = GuardAdbOverWlanUseCase.parseWlanTableId(sampleOutput)

        assertEquals("Should return null when wlan0 not present", null, tableId)
    }

    @Test
    fun discoverWlanTableId_findsActiveWlan0Table() {
        val rules = uiDevice.executeShellCommand("ip rule show")
        println("Current IP rules:\n$rules")

        val tableId = GuardAdbOverWlanUseCase.parseWlanTableId(rules)
        println("Discovered wlan0 table ID: $tableId")

        // Skip test if WiFi is not connected
        assumeNotNull("WiFi must be connected for this test", tableId)

        assertTrue("Table ID should be in valid range (1000+)", tableId!! >= 1000)
    }

    @Test
    fun guardAdbOverWlan_addsRoutingRule() = runTest {
        // Discover wlan0 table
        val rules = uiDevice.executeShellCommand("ip rule show")
        val tableId = GuardAdbOverWlanUseCase.parseWlanTableId(rules)
        assumeNotNull("WiFi must be connected for this test", tableId)
        discoveredTableId = tableId

        println("Discovered wlan0 table: $tableId")

        // Apply the guard
        val result = GuardAdbOverWlanUseCase()(tableId!!)
        assertTrue("Guard should succeed", result is ApiResult.Success)

        // Verify the rule was added
        val updatedRules = uiDevice.executeShellCommand("ip rule show")
        println("Updated IP rules:\n$updatedRules")

        val expectedPattern = "from all uidrange ${GuardAdbOverWlanUseCase.ADB_UID}-${GuardAdbOverWlanUseCase.ADB_UID} lookup $tableId"
        assertTrue(
            "Should find ADB guard rule in ip rule show output",
            updatedRules.contains(expectedPattern)
        )

        // Verify priority
        val priorityPattern = "${GuardAdbOverWlanUseCase.RULE_PRIORITY}:"
        assertTrue(
            "Guard rule should have priority ${GuardAdbOverWlanUseCase.RULE_PRIORITY}",
            updatedRules.contains(priorityPattern)
        )
    }

    @Test
    fun guardAdbOverWlan_ruleHasHigherPriorityThanDefault() = runTest {
        // Discover wlan0 table
        val rules = uiDevice.executeShellCommand("ip rule show")
        val tableId = GuardAdbOverWlanUseCase.parseWlanTableId(rules)
        assumeNotNull("WiFi must be connected for this test", tableId)
        discoveredTableId = tableId

        // Apply the guard
        val result = GuardAdbOverWlanUseCase()(tableId!!)
        assertTrue("Guard should succeed", result is ApiResult.Success)

        // Parse rules to verify priority ordering
        val updatedRules = uiDevice.executeShellCommand("ip rule show")

        // Extract priorities from rules
        val priorityRegex = Regex("""^(\d+):""", RegexOption.MULTILINE)
        val priorities = priorityRegex.findAll(updatedRules)
            .map { it.groupValues[1].toInt() }
            .toList()

        println("Rule priorities in order: $priorities")

        // Guard priority (5000) should appear before higher numbers (default rules are typically 31000+)
        val guardPriorityIndex = priorities.indexOf(GuardAdbOverWlanUseCase.RULE_PRIORITY)
        assertTrue(
            "Guard priority ${GuardAdbOverWlanUseCase.RULE_PRIORITY} should be in the rule list",
            guardPriorityIndex >= 0
        )

        // Verify it comes before the catch-all unreachable rule (32000)
        val unreachableIndex = priorities.indexOf(32000)
        if (unreachableIndex >= 0) {
            assertTrue(
                "Guard rule should come before unreachable rule",
                guardPriorityIndex < unreachableIndex
            )
        }
    }

    @Test
    fun adbUid_isCorrectValue() {
        // UID 2000 is the shell user in Android
        assertEquals("ADB UID should be 2000 (shell user)", 2000, GuardAdbOverWlanUseCase.ADB_UID)

        // Verify via shell command
        val shellId = uiDevice.executeShellCommand("id -u shell").trim()
        assertEquals("Shell UID from device should match constant", "2000", shellId)
    }
}
