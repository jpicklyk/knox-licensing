package net.sfelabs.knoxmoduleshowcase.tests.hdm

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.hdm.GetHdmPolicyUseCase
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
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class HdmTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
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
    fun disableCamera() = runTest {
        val result = SetHdmCameraState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, cameraBitmask ))
    }

    @Test
    fun enableCamera() = runTest {
        val result = SetHdmCameraState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, cameraBitmask))
    }

    @Test
    fun disableMmc() = runTest {
        val result = SetHdmExternalMemoryState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    @Test
    fun enableMmc() = runTest {
        val result = SetHdmExternalMemoryState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, mmcBitmask ))
    }

    //@Test
    fun disableUsb() = runTest {
        val result = SetHdmUsbState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    fun enableUsb() = runTest {
        val result = SetHdmUsbState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, usbBitmask ))
    }

    @Test
    fun disableWifi() = runTest {
        val result = SetHdmWiFiState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    fun enableWifi() = runTest {
        val result = SetHdmWiFiState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, wifiBitmask ))
    }

    @Test
    fun disableBluetooth() = runTest {
        val result = SetHdmBluetoothState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    fun enableBluetooth() = runTest {
        val result = SetHdmBluetoothState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, bluetoothBitmask ))
    }

    @Test
    fun disableGps() = runTest {
        val result = SetHdmGpsState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        println("HDM Policy: $currentPolicy")
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    fun enableGps() = runTest {
        val result = SetHdmGpsState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, gpsBitmask ))
    }

    @Test
    fun disableNfc() = runTest {
        val result = SetHdmNfcState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    @Test
    fun enableNfc() = runTest {
        val result = SetHdmNfcState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, nfcBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun disableMicrophone() = runTest {
        val result = SetHdmMicrophoneState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, micBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun enableMicrophone() = runTest {
        val result = SetHdmMicrophoneState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, micBitmask ))
    }

    @Test
    fun disableModem() = runTest {
        val result = SetHdmModemState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, modemBitmask ))
    }

    @Test
    fun enableModem() = runTest {
        val result = SetHdmModemState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, modemBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun disableSpeaker() = runTest {
        val result = SetHdmSpeakerState(context).invoke(disabled = true)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    /**
     * The XCP HW does not correctly support this HDM control and will be a NO-OP
     */
    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-G736U1"])
    fun enableSpeaker() = runTest {
        val result = SetHdmSpeakerState(context).invoke(disabled = false)
        assert(result is ApiResult.Success && result.data)

        val currentPolicy = GetHdmPolicyUseCase(context).invoke()
        assert(currentPolicy is ApiResult.Success && !featureDisabled(currentPolicy.data, speakerBitmask ))
    }

    @After
    fun resetHardware() = runTest {
        //Ensure that nothing is blocked
        SetHdmPolicyUseCase(context).invoke(0, false)
    }
    private fun featureDisabled(input: Int, bitmask: Int): Boolean {
        return input and bitmask != 0
    }
}