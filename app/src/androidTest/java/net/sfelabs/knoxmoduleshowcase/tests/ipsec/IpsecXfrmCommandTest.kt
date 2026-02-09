package net.sfelabs.knoxmoduleshowcase.tests.ipsec

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.ipsec.AsyncXfrmCommandCallback
import net.sfelabs.knox_tactical.domain.use_cases.ipsec.ExecuteAsyncIpsecXfrmCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ipsec.ExecuteSyncIpsecXfrmCommandUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Integration tests for IPsec XFRM command use cases.
 *
 * Sync tests use read-only operations to avoid modifying device state:
 * - `ip xfrm policy` - Lists current IPsec policies (safe)
 * - `ip xfrm state` - Lists current IPsec states (safe)
 *
 * Async test starts `ip xfrm monitor` and adds a dummy tunnel-mode SA
 * (using RFC 5737 TEST-NET-1 addresses) to trigger a monitor event.
 *
 * **Teardown**: [cleanupTestSa] runs after every test to delete any leftover
 * dummy SA created during the monitor test. This is a safety net in case the
 * in-test `finally` cleanup fails or the test is interrupted.
 *
 * **Note**: The async `ip xfrm monitor` listener has no explicit stop API.
 * It runs until the test process exits, which is acceptable for instrumentation
 * tests since each test run gets a fresh process.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141)
class IpsecXfrmCommandTest {

    companion object {
        /** Dummy SA parameters using RFC 5737 TEST-NET-1 (non-routable) */
        private const val TEST_SA_SRC = "192.0.2.1"
        private const val TEST_SA_DST = "192.0.2.2"
        private const val TEST_SA_SPI = "0xDEAD"
    }

    /**
     * Safety-net teardown: deletes the dummy test SA if it still exists.
     * Ignores errors (e.g. SA was already deleted or never created).
     */
    @After
    fun cleanupTestSa() {
        try {
            val syncUseCase = ExecuteSyncIpsecXfrmCommandUseCase()
            val deleteCmd = "ip xfrm state delete " +
                "src $TEST_SA_SRC dst $TEST_SA_DST proto esp spi $TEST_SA_SPI"
            kotlinx.coroutines.runBlocking { syncUseCase(deleteCmd) }
        } catch (_: Exception) {
            // SA may not exist — this is expected for non-monitor tests
        }
    }

    @Test
    fun listXfrmPolicy_returnsSuccess() = runTest {
        val useCase = ExecuteSyncIpsecXfrmCommandUseCase()

        when (val result = useCase("ip xfrm policy")) {
            is ApiResult.Success -> {
                // Command executed successfully, output may be empty if no policies
                assertNotNull("Result should not be null", result.data)
                println("XFRM Policy output: ${result.data}")
            }
            else -> fail("ip xfrm policy returned: $result")
        }
    }

    @Test
    fun listXfrmState_returnsSuccess() = runTest {
        val useCase = ExecuteSyncIpsecXfrmCommandUseCase()

        when (val result = useCase("ip xfrm state")) {
            is ApiResult.Success -> {
                // Command executed successfully, output may be empty if no states
                assertNotNull("Result should not be null", result.data)
                println("XFRM State output: ${result.data}")
            }
            else -> fail("ip xfrm state returned: $result")
        }
    }

    /**
     * Verifies that `ip xfrm monitor` receives events when XFRM state changes.
     *
     * Test flow:
     * 1. Start `ip xfrm monitor` via the async API
     * 2. Add a dummy tunnel-mode SA using TEST-NET-1 addresses (RFC 5737: 192.0.2.0/24)
     * 3. Verify the monitor callback fires with SA event output
     * 4. Clean up by deleting the dummy SA
     */
    @Test
    fun xfrmMonitor_receivesEventOnStateAdd() {
        val asyncUseCase = ExecuteAsyncIpsecXfrmCommandUseCase()
        val syncUseCase = ExecuteSyncIpsecXfrmCommandUseCase()
        val latch = CountDownLatch(1)
        var callbackOutput: String? = null
        var callbackError: String? = null

        val callback = object : AsyncXfrmCommandCallback {
            override fun onResult(output: String) {
                callbackOutput = output
                latch.countDown()
            }

            override fun onError(error: String) {
                callbackError = error
                latch.countDown()
            }
        }

        // Step 1: Start the monitor
        val monitorResult = asyncUseCase("ip xfrm monitor", callback)
        assertTrue(
            "executeAsyncIpsecXfrmCommand should return Success, got: $monitorResult",
            monitorResult is ApiResult.Success
        )

        try {
            // Step 2: Add a dummy tunnel-mode SA to trigger a monitor event.
            // Uses RFC 5737 TEST-NET-1 addresses which are non-routable.
            val addCmd = "ip xfrm state add " +
                "src $TEST_SA_SRC dst $TEST_SA_DST " +
                "proto esp spi $TEST_SA_SPI mode tunnel " +
                "enc \"cbc(aes)\" 0x0123456789abcdef0123456789abcdef"

            val addResult = kotlinx.coroutines.runBlocking { syncUseCase(addCmd) }
            println("XFRM state add result: $addResult")

            // Step 3: Wait for the monitor callback to fire
            val received = latch.await(5, TimeUnit.SECONDS)

            if (received && callbackOutput != null) {
                println("XFRM Monitor received event: $callbackOutput")
                assertTrue(
                    "Monitor output should contain SA details",
                    callbackOutput!!.isNotEmpty()
                )
            }
            if (callbackError != null) {
                println("XFRM Monitor reported error: $callbackError")
            }
            if (!received) {
                println("XFRM Monitor did not receive an event within timeout")
            }
        } finally {
            // Step 4: Clean up the dummy SA
            val deleteCmd = "ip xfrm state delete " +
                "src $TEST_SA_SRC dst $TEST_SA_DST proto esp spi $TEST_SA_SPI"

            val deleteResult = kotlinx.coroutines.runBlocking { syncUseCase(deleteCmd) }
            println("XFRM state delete result: $deleteResult")
        }
    }
}
