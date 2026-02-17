package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 141.
 *
 * APIs marked with [reflection] are not present in the compile-time SDK JAR and are
 * called at runtime via ReflectingSuspendingUseCase.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 141)
class ApiExistenceV141Tests {

    // region SystemManager

    @Test
    fun isWirelessChargingDisabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isWirelessChargingDisabled")) { "Expected method 'isWirelessChargingDisabled' to exist on SystemManager" }
    }

    @Test
    fun disableWirelessCharging_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableWirelessCharging")) { "Expected method 'disableWirelessCharging' to exist on SystemManager" }
    }

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun executeIptablesCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "executeIptablesCommand")) { "Expected method 'executeIptablesCommand' to exist on SystemManager" }
    }

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun enableIpForwarding_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enableIpForwarding")) { "Expected method 'enableIpForwarding' to exist on SystemManager" }
    }

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun getPLMNAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getPLMNAllowedList")) { "Expected method 'getPLMNAllowedList' to exist on SystemManager" }
    }

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun addPLMNAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "addPLMNAllowedList")) { "Expected method 'addPLMNAllowedList' to exist on SystemManager" }
    }

    @Test
    fun executeAsyncIpsecXfrmCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "executeAsyncIpsecXfrmCommand")) { "Expected method 'executeAsyncIpsecXfrmCommand' to exist on SystemManager" }
    }

    @Test
    fun executeSyncIpsecXfrmCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "executeSyncIpsecXfrmCommand")) { "Expected method 'executeSyncIpsecXfrmCommand' to exist on SystemManager" }
    }

    // endregion

    // region RestrictionPolicy

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun isE911DisabledOverPrivateNetworks_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "isE911DisabledOverPrivateNetworks")) { "Expected method 'isE911DisabledOverPrivateNetworks' to exist on RestrictionPolicy" }
    }

    /** [reflection] Not present in compile-time SDK JAR; called via ReflectingSuspendingUseCase. */
    @Test
    fun disableE911OverPrivateNetworks_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "disableE911OverPrivateNetworks")) { "Expected method 'disableE911OverPrivateNetworks' to exist on RestrictionPolicy" }
    }

    // endregion
}
