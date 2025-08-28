package net.sfelabs.knoxmoduleshowcase.tests.hdm

import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmComponent
import net.sfelabs.knox_tactical.domain.use_cases.hdm.IsHdmPolicySupportedUseCase
import org.junit.Assert.assertTrue
import org.junit.Test

@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class SupportedComponents {

    @Test
    fun testIsHdmPolicySupportedUseCase_Camera() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.CAMERA)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for Camera component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U", "SM-S911U1"])
    fun testIsHdmPolicySupportedUseCase_ExternalMemory() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.EXTERNAL_MEMORY)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for External Memory component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    fun testIsHdmPolicySupportedUseCase_Usb() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.USB)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for USB component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    fun testIsHdmPolicySupportedUseCase_Wifi() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.WIFI)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for WiFi component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    fun testIsHdmPolicySupportedUseCase_Bluetooth() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.BLUETOOTH)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for Bluetooth component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U", "SM-S911U1"])
    fun testIsHdmPolicySupportedUseCase_Gps() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.GPS)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for GPS component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    fun testIsHdmPolicySupportedUseCase_Nfc() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.NFC)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for NFC component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun testIsHdmPolicySupportedUseCase_Microphone() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.MICROPHONE)
        assertTrue("Policy support check failed for Microphone component: ${result.getOrNull()}", result is ApiResult.Success<Boolean> && result.data)
    }

    @Test
    fun testIsHdmPolicySupportedUseCase_Modem() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.MODEM)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for Modem component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun testIsHdmPolicySupportedUseCase_Speaker() = runTest {
        val result = IsHdmPolicySupportedUseCase().invoke(HdmComponent.SPEAKER)
        assert(result is ApiResult.Success<Boolean> && result.data) {
            "Policy support check failed for Speaker component.  Error: ${result.getErrorOrNull()}, Data: ${result.getOrNull()}"
        }
    }
}