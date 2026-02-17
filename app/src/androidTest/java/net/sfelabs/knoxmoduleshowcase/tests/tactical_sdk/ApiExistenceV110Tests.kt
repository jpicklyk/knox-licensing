package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 110.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 110)
class ApiExistenceV110Tests {

    // region SystemManager

    @Test
    fun executeAdbCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "executeAdbCommand")) { "Expected method 'executeAdbCommand' to exist on SystemManager" }
    }

    @Test
    fun getAutoCallPickupState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutoCallPickupState")) { "Expected method 'getAutoCallPickupState' to exist on SystemManager" }
    }

    @Test
    fun setAutoCallPickupState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutoCallPickupState")) { "Expected method 'setAutoCallPickupState' to exist on SystemManager" }
    }

    @Test
    fun getAutoCallNumberList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutoCallNumberList")) { "Expected method 'getAutoCallNumberList' to exist on SystemManager" }
    }

    @Test
    fun addAutoCallNumber_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "addAutoCallNumber")) { "Expected method 'addAutoCallNumber' to exist on SystemManager" }
    }

    @Test
    fun getAutomaticRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutomaticRecordCallEnabledState")) { "Expected method 'getAutomaticRecordCallEnabledState' to exist on SystemManager" }
    }

    @Test
    fun setAutomaticRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutomaticRecordCallEnabledState")) { "Expected method 'setAutomaticRecordCallEnabledState' to exist on SystemManager" }
    }

    @Test
    fun setUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setUsbConnectionType")) { "Expected method 'setUsbConnectionType' to exist on SystemManager" }
    }

    @Test
    fun getUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getUsbDeviceAccessAllowedList")) { "Expected method 'getUsbDeviceAccessAllowedList' to exist on SystemManager" }
    }

    @Test
    fun setUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setUsbDeviceAccessAllowedList")) { "Expected method 'setUsbDeviceAccessAllowedList' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(includeModels = ["SM-X308U"])
    fun disablePOGOKeyboardConnection_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disablePOGOKeyboardConnection")) { "Expected method 'disablePOGOKeyboardConnection' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(includeModels = ["SM-X308U"])
    fun isPOGOKeyboardConnectionDisabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isPOGOKeyboardConnectionDisabled")) { "Expected method 'isPOGOKeyboardConnectionDisabled' to exist on SystemManager" }
    }

    // endregion

    // region SettingsManager

    @Test
    fun getAutoAdjustTouchSensitivity_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getAutoAdjustTouchSensitivity")) { "Expected method 'getAutoAdjustTouchSensitivity' to exist on SettingsManager" }
    }

    @Test
    fun setAutoAdjustTouchSensitivity_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setAutoAdjustTouchSensitivity")) { "Expected method 'setAutoAdjustTouchSensitivity' to exist on SettingsManager" }
    }

    @Test
    fun getHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getHotspot20State")) { "Expected method 'getHotspot20State' to exist on SettingsManager" }
    }

    @Test
    fun setHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setHotspot20State")) { "Expected method 'setHotspot20State' to exist on SettingsManager" }
    }

    @Test
    fun setEthernetAutoConnectionState_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setEthernetAutoConnectionState")) { "Expected method 'setEthernetAutoConnectionState' to exist on SettingsManager" }
    }

    // endregion

    // region RestrictionPolicy

    @Test
    fun setUsbDebuggingEnabled_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "setUsbDebuggingEnabled")) { "Expected method 'setUsbDebuggingEnabled' to exist on RestrictionPolicy" }
    }

    // endregion
}
