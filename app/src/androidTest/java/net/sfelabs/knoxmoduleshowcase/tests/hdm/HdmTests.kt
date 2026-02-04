package net.sfelabs.knoxmoduleshowcase.tests.hdm

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.context.AndroidContextProviderRule
import net.sfelabs.knox.core.testing.rules.AdbUsbRequired
import net.sfelabs.knox.core.testing.rules.AdbUsbRequiredRule
import net.sfelabs.knox.core.testing.rules.AdbWifiRequired
import net.sfelabs.knox.core.testing.rules.AdbWifiRequiredRule
import org.junit.Rule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetSupportedHdmPoliciesUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.IsHdmPolicySupportedUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.IsHdmCameraDisabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmBluetoothState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmCameraState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmExternalMemoryState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmGpsState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmMicrophoneState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmModemState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmNfcState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmPolicyUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmSpeakerState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmUsbState
import net.sfelabs.knox_tactical.domain.use_cases.hdm.SetHdmWiFiState
import net.sfelabs.knox_tactical.domain.policy.hdm.HdmComponent
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking
import kotlin.test.DefaultAsserter.assertTrue

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class HdmTests {
    @get:Rule(order = 0)
    val contextRule = AndroidContextProviderRule()

    @get:Rule(order = 1)
    val adbUsbRule = AdbUsbRequiredRule()

    @get:Rule(order = 2)
    val adbWifiRule = AdbWifiRequiredRule()

    private val cameraBitmask = 1
    private val mmcBitmask = 2
    private val usbBitmask = 4
    private val wifiBitmask = 8
    private val bluetoothBitmask = 16
    private val gpsBitmask = 32
    private val nfcBitmask = 64
    private val micBitmask = 128
    private val modemBitmask = 256
    private val speakerBitmask = 512

    @Test
    fun getSupportedPolicies() = runTest {
        val result = GetSupportedHdmPoliciesUseCase().invoke()
        assert(result is ApiResult.Success)
    }



    @Test
    fun testDisabledUseCase_ReturnsNotSupportedForUnsupportedPolicies() = runTest {
        // This test verifies that disabled use cases properly handle unsupported policies
        // We test with camera since it's commonly available
        val supportResult = IsHdmPolicySupportedUseCase().invoke(HdmComponent.CAMERA)
        val disabledResult = IsHdmCameraDisabledUseCase().invoke()
        
        when (supportResult) {
            is ApiResult.Success -> {
                if (supportResult.data) {
                    // If camera HDM is supported, the disabled use case should return a proper result
                    assert(disabledResult is ApiResult.Success || disabledResult is ApiResult.Error)
                } else {
                    // If camera HDM is not supported, the disabled use case should return NotSupported
                    assert(disabledResult is ApiResult.NotSupported) {
                        "Expected NotSupported for unsupported HDM policy, got: $disabledResult"
                    }
                }
            }
            else -> {
                // If support check fails, the disabled use case should handle it gracefully
                assert(disabledResult is ApiResult.NotSupported || disabledResult is ApiResult.Error)
            }
        }
    }

    @Test
    fun disableCamera() = runTest {
        val result = SetHdmCameraState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, cameraBitmask ))
    }

    @Test
    fun enableCamera() = runTest {
        val result = SetHdmCameraState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, cameraBitmask))
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U", "SM-S911U1"])
    fun disableMmc() = runTest {
        val result = SetHdmExternalMemoryState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U", "SM-S911U1"])
    fun enableMmc() = runTest {
        val result = SetHdmExternalMemoryState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    @Test
    @AdbWifiRequired
    fun disableUsb() = runTest {
        val result = SetHdmUsbState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    @AdbWifiRequired
    fun enableUsb() = runTest {
        val result = SetHdmUsbState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    @AdbUsbRequired
    fun disableWifi() = runTest {
        val result = SetHdmWiFiState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    @AdbUsbRequired
    fun enableWifi() = runTest {
        val result = SetHdmWiFiState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    fun disableBluetooth() = runTest {
        val result = SetHdmBluetoothState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    fun enableBluetooth() = runTest {
        val result = SetHdmBluetoothState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-S911U1", "SM-G736U1", "SM-X308U"])
    fun disableGps() = runTest {
        val result = SetHdmGpsState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        println("HDM Policy: $currentPolicy")
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-S911U1", "SM-G736U1", "SM-X308U"])
    fun enableGps() = runTest {
        val result = SetHdmGpsState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    fun disableNfc() = runTest {
        val result = SetHdmNfcState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    @Test
    fun enableNfc() = runTest {
        val result = SetHdmNfcState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun disableMicrophone() = runTest {
        val result = SetHdmMicrophoneState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, micBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun enableMicrophone() = runTest {
        val result = SetHdmMicrophoneState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, micBitmask ))
    }

    @Test
    fun disableModem() = runTest {
        val result = SetHdmModemState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, modemBitmask ))
    }

    @Test
    fun enableModem() = runTest {
        val result = SetHdmModemState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, modemBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun disableSpeaker() = runTest {
        val result = SetHdmSpeakerState().invoke(true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1", "SM-X308U"])
    fun enableSpeaker() = runTest {
        val result = SetHdmSpeakerState().invoke(false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    @Test
    fun clearHdmPolicies() = runTest {
        val result = SetHdmPolicyUseCase().invoke(0, false)
        assertTrue("Clearing HDM policies failed: $result", result is ApiResult.Success)

        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && currentPolicy.data == 0)
    }


    @Test
    fun confirmNoPolicyInPlace() = runTest {
        val currentPolicy = GetHdmPolicyUseCase().invoke()
        assert(currentPolicy is ApiResult.Success && currentPolicy.data == 0)
    }

    private fun featureDisabled(input: Int, bitmask: Int): Boolean {
        return input and bitmask != 0
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun resetHardware() {
            runBlocking {
                // Ensure that nothing is blocked
                SetHdmPolicyUseCase().invoke(0, false)
            }
        }
    }
}