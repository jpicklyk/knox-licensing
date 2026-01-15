package net.sfelabs.knoxmoduleshowcase.tests.ipsec

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.ipsec.ExecuteSyncIpsecXfrmCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for IPsec XFRM command use cases.
 *
 * These tests use read-only operations only to avoid modifying device state:
 * - `ip xfrm policy` - Lists current IPsec policies (safe)
 * - `ip xfrm state` - Lists current IPsec states (safe)
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141)
class IpsecXfrmCommandTest {

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
}
